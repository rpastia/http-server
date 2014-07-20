package ro.pastia.server.protocol.http;

import ro.pastia.server.protocol.http.exception.InvalidRequestException;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * Helper class that holds logic for dealing with HTTP messages that are streaming in or out of the server
 */

public class HttpIOHelper {

    /**
     * Creates a HttpRequest object by parsing data from an input stream
     *
     * @param   input the input stream from where the HTTP request can be read from
     * @return  the object created from the data that was read
     * @throws  IOException
     * @throws  InvalidRequestException
     * @throws  NullPointerException if the specified InputStream is null.
     */
    public static HttpRequest parseHttpRequest(InputStream input) throws IOException, InvalidRequestException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HttpRequest request = new HttpRequest();

        String line = reader.readLine();
        //parse first line of request, Request-Line
        request.parseRequestLine(line);

        //parse header lines
        while (!line.equals("")) {
            line = reader.readLine();
            request.parseHeaderLine(line);
        }
        return request;
    }

    /**
     * Streams a HttpResponse object through an output stream. It can use gzip if the HttpResponse object has
     * the appropriate header.
     *
     * @param   response the response to be streamed
     * @param   output  the output stream for the response
     * @throws  IOException
     * @throws  NullPointerException if either parameter is null.
     */
    public static void streamHttpResponse(HttpResponse response, OutputStream output) throws IOException {
        boolean useGzip = response.getHeader("Content-Encoding", "").equals("gzip");
        if (useGzip) {
            output.write(response.getStatusLine().getBytes());
            output.write(response.getHeaders().getBytes());
            GZIPOutputStream gz = new GZIPOutputStream(output);
            gz.write(response.getBody().getBytes());
            gz.finish();
        } else {
            output.write(response.toString().getBytes());
        }
    }


}
