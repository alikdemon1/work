package com.alisher.work.arbitor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alisher.work.R;
import com.alisher.work.models.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArbitorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Task task = new Task();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arbitor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.ar_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.ar_tabs);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("newtaskid", getIntent().getStringExtra("newTaskId"));
        task.setId(getIntent().getStringExtra("newTaskId"));
        task.setTitle(getIntent().getStringExtra("newTaskTitle"));
        task.setDesc(getIntent().getStringExtra("newTaskDesc"));
        task.setImage((Bitmap) getIntent().getParcelableExtra("newTaskImage"));
        task.setPrice(Integer.valueOf(getIntent().getStringExtra("newTaskCost")));
        task.setEndTime(new Date(getIntent().getLongExtra("newTaskDeadline", 0)));
        task.setClientId(getIntent().getStringExtra("newTaskClientId"));
    }

    public Task getMyData() {
        return task;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DescriptionFragment(), "DESCRIPTION");
        adapter.addFragment(new AttachmentsFragment(), "ATTACHMENTS");
        adapter.addFragment(new DecisionFragment(), "DECISION");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
