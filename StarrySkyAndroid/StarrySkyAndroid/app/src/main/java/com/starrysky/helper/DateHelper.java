package com.starrysky.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eden on 2017/8/11.
 */

public class DateHelper {
    public static String format(String format,Date date ){
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    };


}
