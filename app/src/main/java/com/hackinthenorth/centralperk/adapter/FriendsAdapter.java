package com.hackinthenorth.centralperk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.entity.Friend;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import java.util.ArrayList;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private static Context context;
    private ArrayList<Friend> friends;
    private SparseBooleanArray selectedItems;

    public FriendsAdapter(Context context, ArrayList<Friend> friends) {
        this.friends = friends;
        this.context = context;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvName;
        public final TextView tvPhoneno;
        public final TextView tvEmail;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvFriendName);
            tvPhoneno = (TextView) itemView.findViewById(R.id.tvFriendPhoneno);
            tvEmail = (TextView) itemView.findViewById(R.id.tvFriendEmail);
            final SQLiteHandler db = new SQLiteHandler(FriendsAdapter.context);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String name = tvName.getText().toString();
                    long phoneno = Long.parseLong(tvPhoneno.getText().toString());
                    String email = tvEmail.getText().toString();
                    db.addFriendInHangout(phoneno,name,email);
                    db.close();
                    Toast.makeText(context,"Contact Added!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_friends_recycler_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tvName.setText(friends.get(position).getName());
        viewHolder.tvPhoneno.setText(friends.get(position).getFriendPhone());
        viewHolder.tvEmail.setText(friends.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
