package com.example.abhishektiwari.smsapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.abhishektiwari.smsapp.data.SmsData;
import com.example.abhishektiwari.smsapp.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishektiwari on 13/08/16.
 */
public class SmsFromSearchActivity extends AppCompatActivity {

    SmsData sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_from_search);

        Intent intent = getIntent();
        sms = intent.getParcelableExtra("SMS_DATA");

        TextView smsBody, timeField;
        smsBody = (TextView) findViewById(R.id.sms_body);
        timeField = (TextView) findViewById(R.id.time);

        smsBody.setText(sms.getBody());
        String dateString = Utility.getDate(sms.getDate(), "yyyy-MM-dd");
        timeField.setText(dateString);

        findViewById(R.id.sms_layout).setOnClickListener(new ClickListener());
    }

    class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            List<SmsData> smsDataList = new ArrayList<>();
            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                uri = Telephony.Sms.CONTENT_URI;
            } else {
                uri = Uri.parse("content://sms");
            }
            String[] projection = {Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.THREAD_ID, Telephony.Sms.DATE, Telephony.Sms.TYPE};
            Cursor c = getContentResolver().query(uri, projection, Telephony.Sms.THREAD_ID + " = " + sms.getThreadId() ,null,"date DESC");
            startManagingCursor(c);

            // Read the sms data and store it in the list
            if(c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    try {
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString();
                        long threadId = Long.parseLong(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)).toString());
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString();
                        long date = Long.parseLong(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString());
                        int type = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)));
                        SmsData sms = new SmsData(number, body, threadId, date, type);
                        smsDataList.add(sms);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        c.moveToNext();
                    }
                }
            }
            Intent intent = new Intent(SmsFromSearchActivity.this, SmsDetails.class);
            intent.putParcelableArrayListExtra("MESSAGES", (ArrayList<? extends Parcelable>) smsDataList);
            startActivity(intent);
            finish();
        }
    }
}
