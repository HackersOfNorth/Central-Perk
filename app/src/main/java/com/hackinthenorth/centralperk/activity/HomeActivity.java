package com.hackinthenorth.centralperk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.adapter.MainPagerAdapter;
import com.hackinthenorth.centralperk.entity.DBConstants;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FloatingActionButton bFab;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = new SQLiteHandler(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        View nav_header = mNavigationView.getHeaderView(0);

        name = (TextView) nav_header.findViewById(R.id.tvName);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MainPagerAdapter(
                getSupportFragmentManager()));
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
        bFab = (FloatingActionButton) findViewById(R.id.fab);
        bFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AddFriendsActivity.class));
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Snackbar.make(main_content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void updateNavDrawerDetails() {
        HashMap<String, String> user = db.getUserDetails();
        db.close();
        name.setText(user.get(DBConstants.KEY_NAME));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                updateNavDrawerDetails();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
