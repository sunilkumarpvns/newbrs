package com.elitecore.corenetvertex.spr.voltdb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VoltDBDateUtil {

    public static final String VOLTDB_DATE_FORMAT = "yyyy-MM-dd hh:MM:ss.SSS000";

    public static String convertDate(long dateParam, String dateFormat) {
        Date date = new Date(dateParam);
        SimpleDateFormat df2 = new SimpleDateFormat(dateFormat);
        return df2.format(date);
    }

}
