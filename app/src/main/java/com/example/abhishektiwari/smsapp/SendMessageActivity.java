package com.example.abhishektiwari.smsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageActivity extends AppCompatActivity {

    EditText message_frame;
    EditText recipent_frame;
    TextView sent_message;
    ImageView send_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Send Message");
        initView();

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = recipent_frame.getText().toString().trim();
                String textSMS = message_frame.getText().toString().trim();
                try{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, textSMS, null, null);
                } catch (Exception e) {
                    Toast.makeText(SendMessageActivity.this, "Message Sending failed !!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    if(!sent_message.getText().toString().equals(textSMS)){
                        sent_message.setText(textSMS);
                    }
                    message_frame.setText("");
                }
            }
        });
    }

    void initView(){
        message_frame = (EditText) findViewById(R.id.message_content);
        recipent_frame = (EditText) findViewById(R.id.contact_number);
        send_message = (ImageView) findViewById(R.id.send_msg);
        sent_message = (TextView) findViewById(R.id.sent_message);
    }

}
