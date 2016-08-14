package com.example.abhishektiwari.smsapp;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by abhishektiwari on 14/08/16.
 */
public class Utility {

    public static String getDate(long milliseconds, String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(milliseconds);
    }

}
