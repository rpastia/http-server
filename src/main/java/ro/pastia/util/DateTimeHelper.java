package ro.pastia.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Radu on 19.07.2014.
 */
public class DateTimeHelper {

    public static final Calendar calendar = Calendar.getInstance();
    public static final SimpleDateFormat httpServerDateFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    static {
        httpServerDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static synchronized String getHttpServerTime() {
        return httpServerDateFormat.format(calendar.getTime());
    }

    public static synchronized String getHttpTime(long timeStamp) {
        Date date = new Date(timeStamp);
        return httpServerDateFormat.format(date);
    }

}
