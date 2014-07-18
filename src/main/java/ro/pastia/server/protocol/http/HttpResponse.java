package ro.pastia.server.protocol.http;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Pastia on 17.07.2014.
 */
public class HttpResponse {
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

    public String toString(){
      return status;
    }

  }

  private static final String CRLF = "\r\n";
  private static final String HTTP_VERSION = "HTTP/1.1";
  private Status status = Status.STATUS_200;
  private Map<String, String> headers = new HashMap<>();

  private String body;

  private String serverName;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getHeader(String header) {
    return headers.get(header);
  }

  public void putHeader(String header, String value) {
    headers.put(header, value);
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public String toString()
  {
    //default Content-Type
    if(!headers.containsKey("Content-Type")){
      headers.put("Content-Type", "text/html; charset=iso-8859-1");
    }

    //automatically populate some headers
    headers.put("Content-Length", Integer.toString(body.getBytes().length));
    //headers.put("Date", ""); //TODO: fix Fri, 18 Jul 2014 17:05:51 GMT
    if(this.serverName!=null) {
      headers.put("Server", this.serverName);
    }

    StringBuilder sb = new StringBuilder();
    //Status Line
    sb.append(HTTP_VERSION + " " + status + CRLF);
    //Headers
    Iterator<Map.Entry<String,String>> iterator = headers.entrySet().iterator();
    while(iterator.hasNext()){
      Map.Entry<String,String> header = iterator.next();
      sb.append(header.getKey());
      sb.append(": ");
      sb.append(header.getValue());
      sb.append(CRLF);
    }
    //CRLF and then Body
    sb.append(CRLF);
    sb.append(body);

    return sb.toString();
  }


}
