package ro.pastia.server;

import ro.pastia.server.protocol.http.HttpResponse;

import java.io.InputStream;

/**
 * Simple class to hold both a HttpResponse and an InputStream. The HttpResponse is built in-memory before being sent
 * to the output stream. It contains all headers and optionally a body. The <code>contentStream</code> member of this
 * class represents a part of the server that can be streamed without being first read in memory.
 */
public class ServerResponse {

    private HttpResponse httpResponse;
    private InputStream contentStream;

    public ServerResponse() {
    }

    public ServerResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public ServerResponse(HttpResponse httpResponse, InputStream contentStream) {
        this.httpResponse = httpResponse;
        this.contentStream = contentStream;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public InputStream getContentStream() {
        return contentStream;
    }

    public void setContentStream(InputStream contentStream) {
        this.contentStream = contentStream;
    }

    public boolean hasContentStream() {
        return (contentStream != null);
    }

}
