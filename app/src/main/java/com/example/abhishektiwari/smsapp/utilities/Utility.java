package com.example.abhishektiwari.smsapp.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by abhishektiwari on 14/08/16.
 */
public class Utility {

    public static String getDate(long milliseconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(milliseconds);
    }

    public static Cursor getCursor(Context context, Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(uri, projections, selection, selectionArgs, sortOrder);
    }

}
