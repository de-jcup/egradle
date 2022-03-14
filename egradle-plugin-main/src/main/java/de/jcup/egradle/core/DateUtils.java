package de.jcup.egradle.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    private static DateFormat SIMPLE_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static String createTimeStamp() {
        return createTimeStamp(new Date());
    }

    public static String createTimeStamp(Date date) {
        return SIMPLE_DATEFORMAT.format(date);
    }
}
