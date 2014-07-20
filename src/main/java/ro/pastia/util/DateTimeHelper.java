package ro.pastia.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper class for dealing with dates
 */
public class DateTimeHelper {

    public static final Calendar calendar = Calendar.getInstance();
    public static final SimpleDateFormat httpServerDateFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    static {
        httpServerDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Returns the current server time in the format required by the HTTP protocol
     *
     * @return the current server time in the format required by the HTTP protocol
     */
    public static synchronized String getHttpServerTime() {
        return httpServerDateFormat.format(calendar.getTime());
    }

    /**
     * Returns the date format string representation of the timestamp, in the format required by the HTTP protocol
     *
     * @param timeStamp the timestamp to be extract the time out of
     * @return the date format string representation of the timestamp
     */
    public static synchronized String getHttpTime(long timeStamp) {
        Date date = new Date(timeStamp);
        return httpServerDateFormat.format(date);
    }

}
