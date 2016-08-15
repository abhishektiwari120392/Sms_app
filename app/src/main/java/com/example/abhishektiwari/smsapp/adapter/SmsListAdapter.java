package com.example.abhishektiwari.smsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.abhishektiwari.smsapp.R;
import com.example.abhishektiwari.smsapp.SmsDetails;
import com.example.abhishektiwari.smsapp.data.SmsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by abhishektiwari on 13/08/16.
 */
public class SmsListAdapter extends ArrayAdapter<List<Long>> {
    // List context
    private final Context context;
    // List values
    private List<Long> threadIds = new ArrayList<>();
    private Map<Long, List<SmsData>> smsMap = new HashMap<>();
    private Map<Long, Long> smsDateThreadIdList = new TreeMap<Long, Long>(Collections.reverseOrder());

    public SmsListAdapter(Context context, Map<Long, List<SmsData>> smsMap) {
        super(context, R.layout.contact_layout);
        this.context = context;
        this.smsMap = smsMap;
        for (long eachThread : smsMap.keySet()) {
            smsDateThreadIdList.put(smsMap.get(eachThread).get(0).getDate(), eachThread);
        }
        for (long date : smsDateThreadIdList.keySet()) {
            threadIds.add(smsDateThreadIdList.get(date));
        }
    }

    @Override
    public int getCount() {
        return threadIds.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_layout, parent, false);
            viewHolder.message = (TextView) convertView.findViewById(R.id.sms_body);
            viewHolder.number = (TextView) convertView.findViewById(R.id.smsNumberText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.number.setText(smsMap.get(threadIds.get(position)).get(0).getNumber());
        viewHolder.message.setText(smsMap.get(threadIds.get(position)).get(0).getBody());
        convertView.setOnClickListener(new ClickListener(position));
        return convertView;
    }

    class ViewHolder {
        TextView number;
        TextView message;
    }

    class ClickListener implements View.OnClickListener{

        int position;

        public ClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SmsDetails.class);
            intent.putParcelableArrayListExtra("MESSAGES", (ArrayList<? extends Parcelable>) smsMap.get(threadIds.get(position)));
            context.startActivity(intent);
        }
    }
}
