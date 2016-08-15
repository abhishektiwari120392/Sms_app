package com.example.abhishektiwari.smsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abhishektiwari.smsapp.adapter.SmsListAdapter;
import com.example.abhishektiwari.smsapp.data.SmsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abhishektiwari on 13/08/16.
 */
public class DashBoardActivity extends AppCompatActivity {

    ListView listView;
    List<SmsData> filteredSmsList = new ArrayList<>();
    ListView searchListResult;
    List<SmsData> smsListAll = new ArrayList<>();
    SearchView searchView;
    int index, top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.new_sms_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, SendMessageActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.message_list);
        searchListResult = (ListView) findViewById(R.id.search_result_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Map<Long, List<SmsData>> smsMap = new HashMap<>();

        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uri = Telephony.Sms.CONTENT_URI;
        } else {
            uri = Uri.parse("content://sms");
        }
        String[] projection = {Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.THREAD_ID, Telephony.Sms.DATE, Telephony.Sms.TYPE};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        startManagingCursor(cursor);

        // Read the sms data and store it in the list
        if(cursor.moveToFirst()) {
            for(int i=0; i < cursor.getCount(); i++) {
                try{
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString();
                    long threadId = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)).toString());
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString();
                    long date = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString());
                    int type = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)).toString());
                    SmsData sms = new SmsData(number, body, threadId, date, type);
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
                    cursor.moveToNext();
                }
            }
        }
        smsListAll.clear();
        for (Long eachThread : smsMap.keySet()){
            for (SmsData eachSms : smsMap.get(eachThread)) {
                smsListAll.add(eachSms);
            }
        }
//         Set smsList in the ListAdapter
        listView.setAdapter(new SmsListAdapter(this, smsMap));
        listView.setSelectionFromTop(index, top);
    }

    @Override
    protected void onPause() {
        super.onPause();
        index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dash_board, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchListResult.getVisibility() == View.GONE) {
                    searchListResult.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                filteredSmsList.clear();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(searchListResult.getVisibility() == View.VISIBLE){
                    searchListResult.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        android.support.v7.widget.SearchView.OnQueryTextListener textChangeListener = new android.support.v7.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                searchListResult.setAdapter(new SearchResultAdapter());
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }

    void filterList(String filter) {
        filteredSmsList.clear();
        for (SmsData eachSms : smsListAll) {
            if (isMatch(eachSms, filter)) {
                filteredSmsList.add(eachSms);
            }
        }
        Collections.sort(filteredSmsList, new Comparator<SmsData>() {
            public int compare(SmsData o1, SmsData o2) {
                if (o1.getDate() == o2.getDate())
                    return 0;
                return o1.getDate() < o2.getDate() ? -1 : 1;
            }
        });
    }

    boolean isMatch(SmsData sms, String filter){
        String description = "";
        description += sms.getBody() + " " + sms.getNumber();
        description = description.toLowerCase();
        return description.contains(filter);
    }

    @Override
    public void onBackPressed() {
        if (searchListResult.getVisibility() == View.VISIBLE) {
            searchView.onActionViewCollapsed();
            filteredSmsList.clear();
            searchListResult.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SearchResultAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return filteredSmsList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredSmsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.search_result_items_layout, parent, false);
            }
            TextView sms_body = (TextView) convertView.findViewById(R.id.sms_body);
            sms_body.setText(filteredSmsList.get(position).getBody());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashBoardActivity.this, SmsFromSearchActivity.class);
                    intent.putExtra("SMS_DATA", filteredSmsList.get(position));
                    searchListResult.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    searchView.onActionViewCollapsed();
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}