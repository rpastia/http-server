package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pastia.server.protocol.http.HttpIOHelper;
import ro.pastia.server.protocol.http.HttpRequest;
import ro.pastia.server.protocol.http.exception.InvalidRequestException;

import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;


public class RequestHandler implements Runnable {

    protected Socket clientSocket;
    protected FileNameMap mimeTypeResolver;

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler(Socket clientSocket, FileNameMap mimeTypeResolver) {
        this.clientSocket = clientSocket;
        this.mimeTypeResolver = mimeTypeResolver;
    }

    public void run() {
        try (
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {
            HttpRequest request = HttpIOHelper.parseHttpRequest(input);
            System.out.print(request.toString());


            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nRequestHandler: " + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time + " " + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (IOException | InterruptedException e) {
            logger.error("Error while processing request: {}", e.toString());
        } catch (InvalidRequestException e) {
            //TODO: Respond with BAD REQUEST
            logger.error("Invalid request: {}", e.getMessage());
        }
    }

}
