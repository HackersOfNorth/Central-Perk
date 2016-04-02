package com.hackinthenorth.centralperk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.entity.Friend;

import java.util.ArrayList;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Friend> friends;

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //TODO item clicked
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
        viewHolder.tvPhoneno.setText(friends.get(position).getFriendId());
        viewHolder.tvEmail.setText(friends.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
