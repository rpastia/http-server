package ro.pastia.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by Radu on 15.07.2014.
 */
public class FileHelperTest {


    @Test
    public void testFileHelper(){
        assertEquals("zip", FileHelper.getExtension("/files/test.zip"));
        assertEquals("", FileHelper.getExtension("/files/test"));
        assertEquals("", FileHelper.getExtension("/files/test/"));
        assertEquals("exe", FileHelper.getExtension("/files/test.zip.exe"));
        assertEquals("zip", FileHelper.getExtension("http://www.test.com/files/test.zip"));
    }
}
