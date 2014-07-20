package ro.pastia.server.protocol.http;

import java.util.HashMap;
import java.util.Map;

/**
 * A HTTP message.
 * <p>
 * Implements all needed functionality to manage the headers of a HTTP message. Other functionality should be
 * implemented in child classes.
 * <p/>
 * Passing <code>null</code> to any method parameter will result in a <code>NullPointerException</code>
 * </p>
 */
public abstract class HttpMessage {

    static final String CRLF = "\r\n";

    /**
     * HashMap to hold all message headers
     */
    Map<String, String> headers = new HashMap<>();

    /**
     * @param header the header to be returned; it is case-sensitive and must not contain the colon
     *               (e.g. "Content-Type")
     * @return value of the header or <code>null</code> if the header was not set
     */
    public String getHeader(String header) {
        return headers.get(header);
    }

    /**
     * @param header       the header to be returned; it is case-sensitive and must not contain the colon
     *                     (e.g. "Content-Type")
     * @param defaultValue the value to be returned if the header was not set
     * @return value of the header or <code>defaultValue</code> if the header was not set
     */
    public String getHeader(String header, String defaultValue) {
        if (!headers.containsKey(header)) {
            return defaultValue;
        }
        return getHeader(header);
    }

    /**
     * @param header the header to be set; it is case-sensitive and must not contain the colon (e.g. "Content-Type")
     * @param value
     */
    public void putHeader(String header, String value) {
        headers.put(header, value);
    }

    /**
     * Sets the header only if it was not already set
     *
     * @param header the header to be set; it is case-sensitive and must not contain the colon (e.g. "Content-Type")
     * @param value
     */
    public void putHeaderIfNotExists(String header, String value) {
        if (!headers.containsKey(header)) {
            headers.put(header, value);
        }
    }

    /**
     * The textual representation of the headers in the format required by the HTTP protocol
     * @return the textual representation of the headers
     */
    public String getHeaders() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey());
            sb.append(": ");
            sb.append(header.getValue());
            sb.append(CRLF);
        }
        return sb.toString();
    }

}
