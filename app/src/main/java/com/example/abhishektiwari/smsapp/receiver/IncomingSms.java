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
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.abhishektiwari.smsapp.DashBoardActivity;
import com.example.abhishektiwari.smsapp.R;
import com.example.abhishektiwari.smsapp.SmsData;
import com.example.abhishektiwari.smsapp.SmsDetails;

import java.util.ArrayList;
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
            String str = "";
            for (int i = 0; i < msg.length; i++) {
                msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS From " + msg[i].getOriginatingAddress();
                str += " :\r\n";
                str += msg[i].getMessageBody().toString();
                str += "\n";
            }

            //---display incoming SMS as a Android Toast---
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            //---Make a notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("New Message");
            mBuilder.setContentText(str);
            mBuilder.setTicker("New Message Alert!");
            mBuilder.setAutoCancel(true);
            mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);

            String number = msg[0].getOriginatingAddress();
            Cursor cursor = null;
            Uri uri = Telephony.Sms.CONTENT_URI;
            String[] projections = {Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE};
            cursor = context.getContentResolver().query(uri, projections, Telephony.Sms.ADDRESS + " = " + number, null, " date ASC");
            long threadId;
//            List<SmsData> smsDataList = new ArrayList<>();
//            if (cursor.moveToFirst()) {
//                threadId = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)));
//                for (int i = 0; i < cursor.getCount(); i++) {
//                    try {
//                        String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString();
//                        long date = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString());
//                        int type = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)).toString());
//                        SmsData sms = new SmsData(number, body, threadId , date, type);
//                        smsDataList.add(sms);
//                    } catch (Exception e) {
//
//                    } finally {
//                        cursor.moveToNext();
//                    }
//                }
//            }
            Intent resultIntent = new Intent(context, DashBoardActivity.class);
//            intent.putParcelableArrayListExtra("MESSAGES", (ArrayList<? extends Parcelable>) smsDataList);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }
}
