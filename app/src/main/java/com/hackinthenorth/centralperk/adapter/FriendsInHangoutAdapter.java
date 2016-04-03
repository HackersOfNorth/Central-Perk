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
public class FriendsInHangoutAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> name;
    ArrayList<String> number;
    LayoutInflater inflate;

    public FriendsInHangoutAdapter(Context context, ArrayList<String> name,
                                   ArrayList<String> number) {
        this.context = context;
        this.name = name;
        this.number = number;
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
            convertView = inflate.inflate(R.layout.friend_list_item, null);
        }
        TextView tname = (TextView) convertView.findViewById(R.id.tcontactName);
        TextView tnumber = (TextView) convertView
                .findViewById(R.id.tcontactPhone);
        tname.setText(name.get(position));
        tnumber.setText(number.get(position));
        return convertView;
    }
}
