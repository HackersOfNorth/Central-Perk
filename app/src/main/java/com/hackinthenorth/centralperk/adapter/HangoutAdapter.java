package com.hackinthenorth.centralperk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hackinthenorth.centralperk.R;

import java.util.ArrayList;

/**
 * Created by Deepankar on 03-04-2016.
 */
public class HangoutAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> name;
    ArrayList<String> message;
    LayoutInflater inflate;

    public HangoutAdapter(Context context, ArrayList<String> name,
                          ArrayList<String> message) {
        this.context = context;
        this.name = name;
        this.message = message;
        inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.hangout_list_item, null);
        }
        TextView tname = (TextView) convertView.findViewById(R.id.tName);
        TextView tmesage = (TextView) convertView
                .findViewById(R.id.tMessage);
        tname.setText(name.get(position));
        tmesage.setText(message.get(position));
        return convertView;
    }
}
