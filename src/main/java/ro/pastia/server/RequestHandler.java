package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pastia.server.protocol.http.HttpIOHelper;
import ro.pastia.server.protocol.http.HttpRequest;
import ro.pastia.server.protocol.http.HttpResponse;
import ro.pastia.server.protocol.http.exception.InvalidRequestException;
import ro.pastia.util.DateTimeHelper;

import java.io.*;
import java.net.FileNameMap;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import static ro.pastia.server.protocol.http.HttpResponse.Status.*;

/**
 * Handles a HTTP request
 */
public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final int STREAMING_BUFFER_SIZE = 1024 * 128;
    private Socket clientSocket;
    private FileNameMap mimeTypeResolver;
    private String basePath;
    private Properties serverConfig;

    /**
     * Constructs a RequestHandler
     *
     * @param clientSocket     the socket to perfrom IO on
     * @param mimeTypeResolver an instance of a FileNameMap to resolve MIME types
     * @param basePath         base path to server documents out of
     * @param serverConfig     contains all other server settings
     */
    public RequestHandler(Socket clientSocket, FileNameMap mimeTypeResolver, String basePath,
                          Properties serverConfig) {
        this.clientSocket = clientSocket;
        this.mimeTypeResolver = mimeTypeResolver;
        this.basePath = basePath;
        this.serverConfig = serverConfig;
    }

    public void run() {
        try (
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream()
        ) {
            HttpRequest httpRequest = HttpIOHelper.parseHttpRequest(input);
            logger.debug("Processing request for: {}", httpRequest.getUri());

            ServerResponse serverResponse = getServerResponse(httpRequest);

            streamResponse(serverResponse, output);
        } catch (IOException e) {
            logger.error("Error while processing request: {}", e.toString());
        } catch (InvalidRequestException e) {
            //TODO: Respond with BAD REQUEST
            logger.error("Invalid request: {}", e.getMessage());
        }
    }

    /**
     * Processes a HttpRequest and constructs the appropriate ServerResponse object
     *
     * @param request the request to be processed
     * @return the server response
     * @throws IOException
     */
    private ServerResponse getServerResponse(HttpRequest request)
            throws IOException {
        HttpResponse response = getNewHttpResponse();
        ServerResponse serverResponse = new ServerResponse();

        switch (request.getMethod()) {
            case GET:
                File file = new File(basePath + request.getUri());
                if (!file.exists()) {
                    handleFileNotFound(request, response);
                }
                if (file.isDirectory()) {
                    handleGetDirectory(request, response, file);
                }
                if (file.isFile()) {
                    handleGetFile(response, file);
                    serverResponse.setContentStream(new BufferedInputStream(new FileInputStream(file)));
                }
                break;
            default:
                //Respond with status 501 Not Implemented for other request methods
                response.setStatus(STATUS_501);
        }

        if (!serverResponse.hasContentStream() && request.acceptsGzip()) {
            response.putHeader("Content-Encoding", "gzip");
        }

        serverResponse.setHttpResponse(response);
        return serverResponse;
    }

    /**
     * Handles a file not found situation
     *
     * @param request  the request that triggered the situation
     * @param response the HttpResponse that will be manipulated
     */
    private void handleFileNotFound(HttpRequest request, HttpResponse response) {
        response.setStatus(STATUS_404);

        response.putHeader("Connection", "close");

        SimpleHTMLDocument html = new SimpleHTMLDocument();
        html.setTitle(STATUS_404.toString());
        html.append("<h1>Not Found</h1>");
        html.append("<p>The requested URL ").append(request.getUri()).append(" was not found on this server.</p>");
        html.append("<hr>");
        html.append(getServerSignature());

        response.setBody(html.toString());
    }

    /**
     * Handles a directory listing.
     *
     * @param request  the request that triggered the directory listing
     * @param response the HttpResponse that will be manipulated
     * @param file     the file object that was obtained from the request Uri
     * @throws IOException
     */
    private void handleGetDirectory(HttpRequest request, HttpResponse response, File file)
            throws IOException {
        if (!request.getUri().endsWith("/")) {
            response.setStatus(STATUS_301);
            response.putHeader("Location", request.getUri() + '/');
        } else {
            response.setBody(getHtmlDirectoryListing(request.getUri(), file));
        }
    }

    /**
     * Handles a file request.
     *
     * @param response the HttpResponse that will be manipulated
     * @param file     the file object that was obtained from the request Uri
     * @throws IOException
     */
    private void handleGetFile(HttpResponse response, File file)
            throws IOException {
        response.putHeader("Last-Modified", DateTimeHelper.getHttpTime(file.lastModified()));
        response.putHeader("Accept-Ranges", "none");
        response.putHeader("Content-Length", String.valueOf(file.length()));
        response.putHeader("Content-Type", mimeTypeResolver.getContentTypeFor(file.getName()));
    }

    /**
     * Streams a ServerResponse object. Handles both the HttpResponse part and the content stream if it exists
     *
     * @param serverResponse the object that needs to be sent to the output stream
     * @param output         the provided output stream
     * @throws IOException
     * @throws NullPointerException if either parameter is null.
     */
    private void streamResponse(ServerResponse serverResponse, OutputStream output) throws IOException {
        HttpResponse httpResponse = serverResponse.getHttpResponse();
        HttpIOHelper.streamHttpResponse(httpResponse, output);

        if (serverResponse.hasContentStream()) {
            try (InputStream in = serverResponse.getContentStream()) {
                byte[] buffer = new byte[STREAMING_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    /**
     * Gets the HTML code for a directory listing.
     *
     * @param uri  URI of the location
     * @param file the file object that was obtained from the request Uri
     * @return HTML code for the directory listing
     * @throws IOException
     * @throws NullPointerException if either parameter is null.
     */
    private String getHtmlDirectoryListing(String uri, File file) throws IOException {
        File[] files = file.listFiles();
        if (files == null) {
            throw new IOException("Can't list files!");
        }

        SimpleHTMLDocument html = new SimpleHTMLDocument();
        html.setTitle("Index of " + uri);
        html.append("<h1>Index of ").append(uri).append("</h1>");

        String childName;
        for (File child : files) {
            if (child.isHidden() || (child.getName().charAt(0) == '.')) {
                continue;
            }
            childName = child.isDirectory() ? child.getName() + "/" : child.getName();
            html.append("<a href=\"").append(childName).append("\">").append(childName).append("</a><br />\n");
        }

        return html.toString();
    }

    private String getServerSignature() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            hostName = "-";
        }
        return "<address>" +
                MultiThreadedServer.NAME + " Server at " + hostName + " Port " +
                serverConfig.getProperty(MultiThreadedServer.PROPERTY_PORT) +
                "</address>";
    }

    private HttpResponse getNewHttpResponse() {
        HttpResponse response = new HttpResponse();
        response.putHeader("Server", MultiThreadedServer.NAME);
        return response;
    }

}
