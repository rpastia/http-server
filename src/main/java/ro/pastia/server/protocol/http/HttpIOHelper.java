package ro.pastia.server.protocol.http;

import ro.pastia.server.protocol.http.exception.InvalidRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpIOHelper {

    public static HttpRequest parseHttpRequest(InputStream input) throws IOException, InvalidRequestException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HttpRequest request = new HttpRequest();

        String line = reader.readLine();
      System.out.println("LINE:" + line);
        //parse first line of request, Request-Line
        request.parseRequestLine(line);

        //parse header lines
        while (!line.equals("")) {
            line = reader.readLine();
            request.parseHeaderLine(line);
        }
        return request;
    }

}
