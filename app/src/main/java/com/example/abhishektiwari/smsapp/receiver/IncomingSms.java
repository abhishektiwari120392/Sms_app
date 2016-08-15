package com.example.abhishektiwari.smsapp.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.abhishektiwari.smsapp.DashBoardActivity;
import com.example.abhishektiwari.smsapp.SmsDetails;
import com.example.abhishektiwari.smsapp.data.SmsData;
import com.example.abhishektiwari.smsapp.utilities.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abhishektiwari on 12/08/16.
 */
public class IncomingSms extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bndl = intent.getExtras();
        SmsMessage[] msg = null;
        if (null != bndl) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bndl.get("pdus");
            msg = new SmsMessage[pdus.length];
            List<String> numbers = new ArrayList<>();
            String str = "";
            List<String> bodies = new ArrayList<>();
            for (int i = 0; i < msg.length; i++) {
                msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS From " + msg[i].getOriginatingAddress();
                str += " :\r\n";
                bodies.add(msg[i].getMessageBody().toString());
                str += msg[i].getMessageBody().toString();
                str += "\n";
                String num = msg[i].getOriginatingAddress();
                numbers.add(num);
            }

            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                uri = Telephony.Sms.CONTENT_URI;
            } else {
                uri = Uri.parse("content://sms");
            }
            String[] projections = {Telephony.Sms._ID, Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.THREAD_ID, Telephony.Sms.DATE, Telephony.Sms.TYPE};
            int mNotificationId = 1;
            for (String eachNum : numbers) {
                List<SmsData> smsDataList = new ArrayList<>();
                long thread_id = 0;
                Cursor cursor = Utility.getCursor(context, uri, projections, "address = ?", new String[]{eachNum}, " date DESC");
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString();
                        thread_id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)).toString());
                        long date = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString());
                        int type = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)).toString());
                        SmsData sms = new SmsData(eachNum, body, thread_id, date, type);
                        smsDataList.add(sms);
                        cursor.moveToNext();
                    }
                    smsDataList.add(0, new SmsData(eachNum, bodies.get(mNotificationId-1), thread_id, new Date().getTime(), 1));
                }
                //---Make a notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setContentTitle("New Message");
                mBuilder.setContentText(str);
                mBuilder.setTicker("New Message Alert!");
                mBuilder.setAutoCancel(true);
                mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);

                Intent resultIntent = new Intent(context, SmsDetails.class);
                resultIntent.putParcelableArrayListExtra("MESSAGES", (ArrayList<? extends Parcelable>) smsDataList);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId++ , mBuilder.build());
            }

        }
    }
}
