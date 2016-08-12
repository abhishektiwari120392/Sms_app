package com.example.abhishektiwari.smsapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by abhishektiwari on 11/08/16.
 */
public class SmsAdapter extends CursorAdapter {

    Context context;
    Cursor cursor;

    public SmsAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
        this.cursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contact_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView number = (TextView) view.findViewById(R.id.smsNumberText);
        TextView message = (TextView) view.findViewById(R.id.sms_body);
        String contactNum = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
        String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
        number.setText(contactNum);
        message.setText(body);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }
}
