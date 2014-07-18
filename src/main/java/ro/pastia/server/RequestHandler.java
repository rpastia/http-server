package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pastia.server.protocol.http.HttpIOHelper;
import ro.pastia.server.protocol.http.HttpRequest;
import ro.pastia.server.protocol.http.HttpResponse;
import ro.pastia.server.protocol.http.exception.InvalidRequestException;

import java.io.*;
import java.net.FileNameMap;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import static ro.pastia.server.protocol.http.HttpResponse.Status.*;


public class RequestHandler implements Runnable {

    Socket clientSocket;
    FileNameMap mimeTypeResolver;
    String basePath;
    Properties serverConfig;

    char pathSeparator = '/';

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

  public RequestHandler(Socket clientSocket, FileNameMap mimeTypeResolver, String basePath,
                        Properties serverConfig) {
    this.clientSocket = clientSocket;
    this.mimeTypeResolver = mimeTypeResolver;
    this.basePath = basePath;
    this.serverConfig = serverConfig;
  }

    public void run() {
        try (
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {
            HttpRequest request = HttpIOHelper.parseHttpRequest(input);
            System.out.print(request.toString());
            streamResponseForRequest(request, output);



            //########### GARBAGE: ###########
            long time = System.currentTimeMillis();
            System.out.println("Request processed: " + time + " " + Thread.currentThread().getId());
        } catch (IOException e) {
            logger.error("Error while processing request: {}", e.toString());
        } catch (InvalidRequestException e) {
            //TODO: Respond with BAD REQUEST
            logger.error("Invalid request: {}", e.getMessage());
        }
    }

  private void streamResponseForRequest(HttpRequest request, OutputStream output)
      throws IOException {
    switch (request.getMethod()) {
      case GET:
        File file = new File(basePath + request.getUri());
        if(!file.exists()) {
          handleFileNotFound(request, output);
        }
        if (file.isDirectory()) {
          handleGetDirectory(request, output, file);
        }

        break;
      default:
    }


  }

  private void handleGetDirectory(HttpRequest request, OutputStream output, File file)
      throws IOException {
    HttpResponse response = getNewHttpResponse();

    if(!request.getUri().endsWith("/")) {
      response.setStatus(STATUS_301);
      response.putHeader("Location", request.getUri() + '/');
    } else {
      response.setBody( getHtmlDirectoryListing(request.getUri(), file) );
    }

    //System.out.print(response.toString());
    output.write(response.toString().getBytes());
  }

  private void handleFileNotFound(HttpRequest request, OutputStream output) throws IOException {
    HttpResponse response = getNewHttpResponse();
    response.setStatus(STATUS_404);

    response.putHeader("Connection", "close"); //TODO:Chceck
    //response.putHeader("Content-Encoding", "gzip"); //TODO:Check
    //response.putHeader("Vary", "Accept-Encoding"); //TODO:Check


    SimpleHTMLDocument html = new SimpleHTMLDocument();
    html.setTitle(STATUS_404.toString());
    html.append("<h1>Not Found</h1>");
    //TODO: URI vs URL
    html.append("<p>The requested URL " + request.getUri() + " was not found on this server.</p>");
    html.append("<hr>");
    html.append(getSignature());

    response.setBody(html.toString());
    System.out.print(response.toString());
    output.write(response.toString().getBytes());
  }

  private String getHtmlDirectoryListing(String uri, File file)
      throws IOException {
    File[] files = file.listFiles();
    if(files==null) {
      throw new IOException("Can't list files!");
    }

    SimpleHTMLDocument html = new SimpleHTMLDocument();
    html.setTitle("Index of " + uri);
    html.append("<h1>Index of " + uri + "</h1>");

    String childName;
    for (File child : files) {
      childName = child.isDirectory() ? child.getName()+"/" : child.getName();
      html.append("<a href=\"" + childName + "\">" + childName + "</a><br />\n");
    }

    return html.toString();
  }

  private String getSignature() {
    String hostName;
    try {
      hostName = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException e) {
      hostName = "-";
    }
    return "<address>" +
        serverConfig.getProperty(MultiThreadedServer.PROPERTY_SERVER_NAME) +
        " Server at " + hostName + " Port " +
        serverConfig.getProperty(MultiThreadedServer.PROPERTY_SERVER_PORT) +
        "</address>";
  }

  private HttpResponse getNewHttpResponse(){
    HttpResponse response = new HttpResponse();
    response.setServerName(serverConfig.getProperty(MultiThreadedServer.PROPERTY_SERVER_NAME));
    return response;
  }

}
