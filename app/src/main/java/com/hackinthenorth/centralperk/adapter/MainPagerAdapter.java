package com.hackinthenorth.centralperk.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hackinthenorth.centralperk.fragment.MyFriendsFragment;
import com.hackinthenorth.centralperk.fragment.MyHangoutsFragment;


public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if (position == 0)
            frag = new MyHangoutsFragment();
        if (position == 1)
            frag = new MyFriendsFragment();
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Hangouts";
        if (position == 1)
            return "Friends";
        return null;
    }
}