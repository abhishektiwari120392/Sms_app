package com.example.abhishektiwari.smsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abhishektiwari on 09/08/16.
 */
public class SmsData implements Parcelable{

    private String number;
    private String body;
    private long threadId;
    private long date;
    // received=1  sent=2
    private int type;

    public SmsData(String number, String body, long threadId, long date) {
        this.number = number;
        this.body = body;
        this.threadId = threadId;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private SmsData (Parcel in) {
        number = in.readString();
        body = in.readString();
        threadId = in.readLong();
        date = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(body);
        dest.writeLong(threadId);
        dest.writeLong(date);
    }

    public static final Parcelable.Creator<SmsData> CREATOR
            = new Parcelable.Creator<SmsData>() {

        public SmsData createFromParcel(Parcel in) {
            return new SmsData(in);
        }

        @Override
        public SmsData[] newArray(int size) {
            return new SmsData[size];
        }

    };
}
