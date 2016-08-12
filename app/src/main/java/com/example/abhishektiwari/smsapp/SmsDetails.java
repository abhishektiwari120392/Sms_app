package com.example.abhishektiwari.smsapp;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SmsDetails extends AppCompatActivity {

    ImageView sendSms;
    EditText smsBody;
    String phoneNo;
    List<SmsData> smsList = new ArrayList<>();
    Long threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();

        ListView listView = (ListView) findViewById(R.id.list_view);
        ArrayList<Parcelable> smsDataList = getIntent().getParcelableArrayListExtra("MESSAGES");
        for (Parcelable eachParcel : smsDataList) {
            smsList.add((SmsData) eachParcel);
        }
        phoneNo = smsList.get(0).getNumber();
        threadId = smsList.get(0).getThreadId();
        MessageAdapter adapter = new MessageAdapter(smsList);
        setTitle(smsList.get(0).getNumber());
        listView.setAdapter(adapter);
        sendSms.setOnClickListener(new ClickListener());
    }

    void initView(){
        sendSms = (ImageView) findViewById(R.id.send_sms);
        smsBody = (EditText) findViewById(R.id.sms_text);
    }

    class MessageAdapter extends BaseAdapter {

        List<SmsData> smsDataList = new ArrayList<>();

        public MessageAdapter(List<SmsData> smsDataList){
            this.smsDataList = smsDataList;
            Collections.reverse(this.smsDataList);
        }

        @Override
        public int getCount() {
            return smsDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return smsDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.message_item_layout, parent, false);
                viewHolder.message = (TextView) convertView.findViewById(R.id.message);
                viewHolder.date = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.message.setText(smsDataList.get(position).getBody());
            viewHolder.date.setText(""+smsDataList.get(position).getDate());
            return convertView;
        }

        class ViewHolder {
            TextView message;
            TextView date;
        }
    }

    class ClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send_sms :
                    String textSMS = "";
                    try{
                        SmsManager smsManager = SmsManager.getDefault();
                        textSMS = smsBody.getText().toString();
                        smsManager.sendTextMessage(phoneNo, null, textSMS, null, null);
                    } catch (Exception e) {
                        Toast.makeText(SmsDetails.this, "Message Sending failed !!!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } finally {
                        smsBody.setText("");
                    }
                break;
            }
        }
    }

}
