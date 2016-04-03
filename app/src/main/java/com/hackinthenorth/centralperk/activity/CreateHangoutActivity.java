package com.hackinthenorth.centralperk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.adapter.FriendsAdapter;
import com.hackinthenorth.centralperk.entity.Friend;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import java.util.ArrayList;

public class CreateHangoutActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onResume() {
        super.onResume();
        populateRecyclerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hangout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreateHangout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int scrollPosition = 0;
        mRecyclerView = (RecyclerView) findViewById(R.id.hangouts_friends_recycler_view);
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }
    public void populateRecyclerView() {
        SQLiteHandler db = new SQLiteHandler(this);
        ArrayList<Friend> friends = db.getUserFriendDetails();
        mAdapter = new FriendsAdapter(this, friends);
        mRecyclerView.setAdapter(mAdapter);
    }
}
