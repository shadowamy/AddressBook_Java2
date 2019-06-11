package com.example.addressbook_java2.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.addressbook_java2.Entity.Contact;
import com.example.addressbook_java2.R;

import java.util.List;

public class AllContactAdapter extends ArrayAdapter {

    private final int resourceId;

    public AllContactAdapter(Context context, int textViewResourceId, List<Contact> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        Contact contact = (Contact) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView textView_name = view.findViewById(R.id.textView_name);
        TextView textView_phone = view.findViewById(R.id.textView_phone);

        textView_name.setText(contact.getName());
        textView_phone.setText(contact.getPhonenumber());

        return view;

    }
}
