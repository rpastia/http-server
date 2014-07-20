package ro.pastia.server.protocol.http;

import org.junit.Test;
import ro.pastia.server.protocol.http.exception.InvalidRequestException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HttpRequestTest {

    @Test
    public void testGoodRequestLine() {
        HttpRequest httpRequest = new HttpRequest();
        try {
            httpRequest.parseRequestLine("GET / HTTP/1.1");
        } catch (InvalidRequestException e) {
            fail("InvalidRequestException thrown!");
        }

        assertEquals("HTTP/1.1", httpRequest.getHttpVersion());
        assertEquals("GET", httpRequest.getMethod().toString());
        assertEquals("/", httpRequest.getUri());
    }

    @Test
    public void testBadMethodRequestLine() {
        HttpRequest httpRequest = new HttpRequest();
        try {
            httpRequest.parseRequestLine("BAD / HTTP/1.1");
            fail("InvalidRequestException should have been thrown!");
        } catch (InvalidRequestException ignored) {
        }
    }

    @Test
    public void testEmptyRequestLine() {
        HttpRequest httpRequest = new HttpRequest();
        try {
            httpRequest.parseRequestLine("");
            fail("InvalidRequestException should have been thrown!");
        } catch (InvalidRequestException ignored) {
        }
    }


}
