package ro.pastia.server.protocol.http;

import ro.pastia.util.DateTimeHelper;

/**
 * A HTTP response.
 * <p>
 * Stores a HTTP response.
 * </p>
 */
public class HttpResponse extends HttpMessage {

    private static final String HTTP_VERSION = "HTTP/1.1";

    /**
     * Status code of the response; defaults to <code>200 OK</code>
     */
    private Status status = Status.STATUS_200;
    private String body;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getBody() {
        return (body == null) ? "" : body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toString() {
        return getStatusLine() + getHeaders() + getBody();
    }

    public String getStatusLine(){
        return HTTP_VERSION + " " + getStatus() + CRLF;
    }

    /**
     * The textual representation of the headers; also adds some headers with default values if they are missing,
     * and calculates values for headers that are populated automatically
     * @return the textual representation of the headers
     */
    public String getHeaders() {
        //Headers with default values:
        putHeaderIfNotExists("Content-Type", "text/html; charset=iso-8859-1");
        putHeaderIfNotExists("Vary", "Accept-Encoding");
        putHeaderIfNotExists("Content-Length", Integer.toString(getBody().getBytes().length));

        //Headers that are populated automatically:
        headers.put("Date", DateTimeHelper.getHttpServerTime());

        //Status Line + Headers + CRLF before body
        return super.getHeaders() + CRLF;
    }

    /**
     * Possible statuses as retrived from http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
     */
    public enum Status {
        STATUS_100("100 Continue"),
        STATUS_101("101 Switching Protocols"),
        STATUS_200("200 OK"),
        STATUS_201("201 Created"),
        STATUS_202("202 Accepted"),
        STATUS_203("203 Non-Authoritative Information"),
        STATUS_204("204 No Content"),
        STATUS_205("205 Reset Content"),
        STATUS_206("206 Partial Content"),
        STATUS_300("300 Multiple Choices"),
        STATUS_301("301 Moved Permanently"),
        STATUS_302("302 Found"),
        STATUS_303("303 See Other"),
        STATUS_304("304 Not Modified"),
        STATUS_305("305 Use Proxy"),
        STATUS_307("307 Temporary Redirect"),
        STATUS_400("400 Bad Request"),
        STATUS_401("401 Unauthorized"),
        STATUS_402("402 Payment Required"),
        STATUS_403("403 Forbidden"),
        STATUS_404("404 Not Found"),
        STATUS_405("405 Method Not Allowed"),
        STATUS_406("406 Not Acceptable"),
        STATUS_407("407 Proxy Authentication Required"),
        STATUS_408("408 Request Time-out"),
        STATUS_409("409 Conflict"),
        STATUS_410("410 Gone"),
        STATUS_411("411 Length Required"),
        STATUS_412("412 Precondition Failed"),
        STATUS_413("413 Request Entity Too Large"),
        STATUS_414("414 Request-URI Too Large"),
        STATUS_415("415 Unsupported Media Type"),
        STATUS_416("416 Requested range not satisfiable"),
        STATUS_417("417 Expectation Failed"),
        STATUS_500("500 Internal Server Error"),
        STATUS_501("501 Not Implemented"),
        STATUS_502("502 Bad Gateway"),
        STATUS_503("503 Service Unavailable"),
        STATUS_504("504 Gateway Time-out"),
        STATUS_505("505 HTTP Version not supported");

        private final String status;

        private Status(String status) {
            this.status = status;
        }

        public String toString() {
            return status;
        }
    }

}
