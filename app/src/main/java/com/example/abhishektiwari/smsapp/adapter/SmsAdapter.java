package com.example.abhishektiwari.smsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.abhishektiwari.smsapp.R;

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
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView number = (TextView) view.findViewById(R.id.smsNumberText);
        TextView message = (TextView) view.findViewById(R.id.sms_body);
        String contactNum = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString();
        String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString();
        number.setText(contactNum);
        message.setText(body);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, SmsThreadDetails.class);
//                intent.putExtra("THREAD_ID", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)));
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }
}
