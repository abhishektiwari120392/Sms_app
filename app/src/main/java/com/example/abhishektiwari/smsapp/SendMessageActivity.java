package com.example.abhishektiwari.smsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity {

    EditText message_frame;
    EditText recipent_frame;
    TextView sent_message;
    ImageView send_message;
    List<Contact> contactListAll = new ArrayList<>();
    List<Contact> filteredContactList = new ArrayList<>();
    ListView searchListView;
    RelativeLayout smsLayout;
    ContactListAdapter adapter;
    RelativeLayout selectedContactLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Send Message");
        initView();

        new FetchContacts().execute();
        filteredContactList.clear();
        adapter = new ContactListAdapter(filteredContactList);
        searchListView.setAdapter(adapter);
        recipent_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsLayout.setVisibility(View.GONE);
                searchListView.setVisibility(View.VISIBLE);
            }
        });

        recipent_frame.addTextChangedListener(textWatcher);
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean messageSent = true;
                String number = "";
                String message = "";
                if (selectedContactLayout.getVisibility() == View.VISIBLE) {
                    Contact contact = (Contact) recipent_frame.getTag();
                    number = contact.getNumber();
                } else if (recipent_frame.getText().toString().length() > 0){
                    number = recipent_frame.getText().toString().trim();
                }
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    message = message_frame.getText().toString();
                    smsManager.sendTextMessage(number, null, message, null, null);
                } catch (Exception e) {
                    messageSent = false;
                    Toast.makeText(SendMessageActivity.this, "Message Sending failed !!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    if (messageSent) {
                        sent_message.setText(message);
                        message_frame.setText("");
                    }
                }
            }
        });
        selectedContactLayout.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipent_frame.setTag(null);
                selectedContactLayout.setVisibility(View.GONE);
                ((Button)selectedContactLayout.findViewById(R.id.selected_contact)).setText("");
            }
        });
    }

    void initView(){
        message_frame = (EditText) findViewById(R.id.message_content);
        recipent_frame = (EditText) findViewById(R.id.contact_number);
        send_message = (ImageView) findViewById(R.id.send_msg);
        sent_message = (TextView) findViewById(R.id.sent_message);
        searchListView = (ListView) findViewById(R.id.filter_contact_list);
        smsLayout = (RelativeLayout) findViewById(R.id.sms_layout);
        selectedContactLayout = (RelativeLayout) findViewById(R.id.selected_contact_layout);
    }

    void filterSearchList(String filterText) {
        filteredContactList.clear();
        for (Contact eachContact : contactListAll) {
            String descriptionString = eachContact.getName() + "_" + eachContact.getNumber();
            descriptionString.toLowerCase();
            if (descriptionString.contains(filterText.toLowerCase()))
                filteredContactList.add(eachContact);
        }
    }

    class ContactListAdapter extends BaseAdapter {

        List<Contact> contacts = new ArrayList<>();

        public ContactListAdapter(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_layout, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.sms_body);
                viewHolder.number = (TextView) convertView.findViewById(R.id.smsNumberText);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(contacts.get(position).getName());
            viewHolder.number.setText(contacts.get(position).getNumber());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recipent_frame.setTag(contacts.get(position));
                    searchListView.setVisibility(View.GONE);
                    smsLayout.setVisibility(View.VISIBLE);
                    selectedContactLayout.setVisibility(View.VISIBLE);
                    ((Button)selectedContactLayout.findViewById(R.id.selected_contact)).setText(contacts.get(position).getName());
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        TextView name;
        TextView number;
    }

    class FetchContacts extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            String[] projections = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            Cursor cursor = getContentResolver().query(uri, projections, null, null, null);
            if (cursor.moveToFirst()) {
                for (int i=0; i < cursor.getCount(); i++) {
                    try{
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        if (cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1){
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            if (phones.moveToFirst()) {
                                for (int index = 0; index < phones.getCount(); index++) {
                                    try {
                                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        Contact contact = new Contact(contactName, number);
                                        contactListAll.add(contact);
                                    } catch (Exception e) {

                                    } finally {
                                        phones.moveToNext();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    } finally {
                        cursor.moveToNext();
                    }
                }
            }
            return true;
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (filteredContactList.size() == 0) {
                    searchListView.setVisibility(View.GONE);
                    smsLayout.setVisibility(View.VISIBLE);
                }
                filterSearchList(s.toString());
                if (searchListView.getVisibility() == View.GONE && filteredContactList.size() > 0) {
                    searchListView.setVisibility(View.VISIBLE);
                    smsLayout.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
