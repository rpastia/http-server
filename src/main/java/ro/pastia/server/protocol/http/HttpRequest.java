package ro.pastia.server.protocol.http;

import ro.pastia.server.protocol.http.exception.InvalidRequestException;

/**
 * A HTTP request.
 * <p>
 * Stores a HTTP request and can parse request lines.
 * </p>
 */
public class HttpRequest extends HttpMessage {

    private String httpVersion;
    private Method method;
    private String uri;

    /**
     * Parses the line containing the <code>Request-Line</code> portion of the request
     * as specified by <code>RFC 2616</code>
     *
     * @throws InvalidRequestException
     */
    public void parseRequestLine(String line) throws InvalidRequestException {
        if (line == null) {
            throw new InvalidRequestException("Request Line was NULL");
        }

        String[] splits = line.split("\\s");
        if (splits.length != 3) {
            throw new InvalidRequestException("Bad Request Line");
        }

        try {
            setMethod(Method.valueOf(splits[0]));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Bad Method: " + splits[0]);
        }

        setUri(splits[1]);
        setHttpVersion(splits[2]);
    }

    /**
     * Parses a line containing a header specification of the request
     *
     * @throws InvalidRequestException
     */
    public void parseHeaderLine(String line) throws InvalidRequestException {
        if (line.equals("")) {
            return;
        }
        String[] splits = line.split(": ");

        if (splits.length != 2) {
            throw new InvalidRequestException("Bad Header Line: " + line);
        }

        headers.put(splits[0], splits[1]);
    }

    /**
     * Returns the request line of the request
     * @return the request line of the request
     */
    public String getRequestLine(){
        return String.valueOf(getMethod()) + " " + getUri() + " " + getHttpVersion() + CRLF;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    /**
     * @return whether the client that made the request accepts gzip content encoding or not
     */
    public boolean acceptsGzip() {
        return headers.containsKey("Accept-Encoding") && headers.get("Accept-Encoding").contains("gzip");
    }

    /**
     * @return whether the client that made the request wants connection keep-alive
     */
    public boolean acceptsConnectionKeepAlive() {
        //TODO: This is not final
        //Keep-Alive defaults to true for HTTP/1.1
        boolean keepAlive = (this.getHttpVersion().equals("HTTP/1.1"));
        if(headers.containsKey("Connection") && headers.get("Connection").equals("Close")){
            keepAlive = false;
        }
        return keepAlive;
    }

    public String toString() {
        return getRequestLine() + super.getHeaders();
    }

    public enum Method {
        OPTIONS,
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        TRACE,
        CONNECT
    }


}
