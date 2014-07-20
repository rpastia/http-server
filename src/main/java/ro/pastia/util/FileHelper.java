package ro.pastia.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for file related logic
 */
public class FileHelper {

    private static final Pattern extensionPattern = Pattern.compile(".*\\.([a-z0-9\\-]+)$");

    /**
     * Returns the extension of a file
     *
     * @param filename The filename (e.g. "/files/archive.zip")
     * @return The extension (e.g. "zip"). Returns an empty string if the file has no extension.
     */
    public static String getExtension(String filename) {
        Matcher m = extensionPattern.matcher(filename);
        return m.matches() ? m.group(1) : "";
    }


}
