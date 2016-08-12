package com.example.abhishektiwari.smsapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, SendMessageActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.message_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Map<Long, List<SmsData>> smsMap = new HashMap<>();

//        Uri uri = Uri.parse("content://sms/inbox");
        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uri = Telephony.Sms.CONTENT_URI;
        } else {
            uri = Uri.parse("content://sms");
        }
        Cursor c = getContentResolver().query(uri, null, null ,null,"date" + " DESC");
        startManagingCursor(c);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
            for(int i=0; i < c.getCount(); i++) {
                try{
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString();
                    long threadId = Long.parseLong(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)).toString());
                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString();
                    long date = Long.parseLong(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString());
                    SmsData sms = new SmsData(number, body, threadId, date);
                    if (smsMap.containsKey(sms.getThreadId())) {
                        smsMap.get(sms.getThreadId()).add(sms);
                    } else {
                        List<SmsData> dataList = new ArrayList<>();
                        dataList.add(sms);
                        smsMap.put(sms.getThreadId(), dataList);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    c.moveToNext();
                }
            }
        }

        // Set smsList in the ListAdapter
        listView.setAdapter(new SmsListAdapter(this, smsMap));
//        listView.setAdapter(new SmsAdapter(DashBoardActivity.this, c));
//        c.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
