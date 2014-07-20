package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pastia.util.FileHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Resolves MIME type for a wide array of file extensions by using the static <code>mime.types.txt</code> file resource.
 * The file was retrived from https://svn.apache.org/repos/asf/httpd/httpd/trunk/docs/conf/mime.types
 */
public class MimeTypeResolver implements FileNameMap {

    private static final String DEFAULT = "application/octet-stream";
    private static final Logger logger = LoggerFactory.getLogger(MimeTypeResolver.class);

    static {
        instance = new MimeTypeResolver();
    }

    private static MimeTypeResolver instance;
    private Map<String, String> extToMimeMap;

    private MimeTypeResolver() {
        extToMimeMap = new ConcurrentHashMap<>();
        initializeMimeTypes();
    }

    public static MimeTypeResolver getInstance() {
        return instance;
    }

    private void initializeMimeTypes() {
        try (
                InputStream mimeFileStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("mime.types.txt");
                BufferedReader mimeFileReader = new BufferedReader(new InputStreamReader(mimeFileStream))
        ) {
            String line;
            while ((line = mimeFileReader.readLine()) != null) {
                loadMimeLine(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't load mime types mappings!", e);
        }
        logger.info("Initialized mime types! {} mappings loaded", extToMimeMap.size());
    }


    private void loadMimeLine(String line) {
        line = line.trim();
        if (line.charAt(0) == '#') {
            return;
        }
        String[] pieces = line.split("\\s+");
        if (pieces.length < 2) {
            logger.warn("Bad line in mime types file: {}", line);
            return;
        }
        String mimeType = pieces[0];
        for (int i = 1; i < pieces.length; i++) {
            extToMimeMap.put(pieces[i], mimeType);
        }
    }

    /**
     * Return the MIME type from a file name (possibly including a path)
     *
     * @param name The name (e.g. "/pictures/dome.gif")
     * @return The MIME type (e.g. "image/gif")
     */
    @Override
    public String getContentTypeFor(String name) {
        return getContentTypeForExtension(FileHelper.getExtension(name));

    }

    /**
     * Return the MIME type for a specific file extension
     *
     * @param ext The extension (e.g. "gif")
     * @return The MIME type (e.g. "image/gif")
     */
    public String getContentTypeForExtension(String ext) {
        String type;
        type = extToMimeMap.get(ext.toLowerCase());

        return (type == null) ? DEFAULT : type;
    }


}
