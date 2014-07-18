package ro.pastia.server.protocol.http;

import ro.pastia.server.protocol.http.exception.InvalidRequestException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Radu on 15.07.2014.
 */
public class HttpRequest {

    public enum Method {
        OPTIONS("OPTIONS"),
        GET("GET"),
        HEAD("HEAD"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        TRACE("TRACE"),
        CONNECT("CONNECT"),
        UNRECOGNIZED(null);

        private final String value;

        private Method(String value) {
            this.value = value;
        }
    }

    private Method method;
    private String uri;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>();

    protected void parseRequestLine(String line) throws InvalidRequestException {
        String[] splits = line.split("\\s");

        if(splits.length!=3) {
            throw new InvalidRequestException("Bad Request Line");
        }

        try {
            method = Method.valueOf(splits[0]);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Bad Method: " + splits[0]);
        }

        uri = splits[1];
        httpVersion = splits[2];
    }

    protected void parseHeaderLine(String line) throws InvalidRequestException {
        if(line.equals("")){
            return;
        }
        String[] splits = line.split(": ");

        if(splits.length!=2) {
            throw new InvalidRequestException("Bad Header Line: " + line);
        }

        headers.put(splits[0], splits[1]);
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

    public String getHeader(String header) {
        return headers.get(header);
    }

    public void putHeader(String header, String value) {
        headers.put(header, value);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(method + " " + uri + " " + httpVersion + "\n");

        Iterator<Map.Entry<String,String>> iterator = headers.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> header = iterator.next();
            sb.append(header.getKey());
            sb.append(": ");
            sb.append(header.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }


}
