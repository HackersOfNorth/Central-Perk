package com.hackinthenorth.centralperk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.adapter.FriendsAdapter;
import com.hackinthenorth.centralperk.entity.Friend;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import java.util.ArrayList;


public class MyFriendsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        populateRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int scrollPosition = 0;
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.friends_recycler_view);
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    public void populateRecyclerView() {
        SQLiteHandler db = new SQLiteHandler(getActivity());
        ArrayList<Friend> friends = db.getUserFriendDetails();
        mAdapter = new FriendsAdapter(getActivity(), friends);
        mRecyclerView.setAdapter(mAdapter);
    }
}

