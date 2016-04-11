package com.alisher.work.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.fragments.ClientFragment;
import com.alisher.work.fragments.PerformerFragment;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView profileImg;
    private TextView emailText;
    private NavigationView navigationView;
    private ParseUser currentUser;
    private TextView nameText, balanceText, frozenBalanceText;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = ParseUser.getCurrentUser();
        initToolbar();
        initViewPager();
        initTablayout();
        initDrawer();
        initProfile();
    }

    private void initProfile() {
        View header = navigationView.getHeaderView(0);
        emailText = (TextView) header.findViewById(R.id.profile_email);
        nameText = (TextView) header.findViewById(R.id.profile_name);
        profileImg = (ImageView) header.findViewById(R.id.imageView);
        balanceText = (TextView) header.findViewById(R.id.userBalance);
        frozenBalanceText = (TextView) header.findViewById(R.id.userFrozenBalance);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    for (ParseUser user : list) {
                        final String name = user.getString("firstName") + " " + user.getString("lastName");
                        final String email = user.getUsername();
                        ParseFile file = user.getParseFile("photo");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    nameText.setText(name);
                                    emailText.setText(email);
                                    photo = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profileImg.setImageBitmap(photo);
                                } else {
                                }
                            }
                        });
                    }
                } else {
                    Log.e("MainActivity", e.getMessage());
                }
            }
        });
        ParseQuery<ParseObject> queryBal = ParseQuery.getQuery("Achievement");
        queryBal.whereEqualTo("userId", currentUser.getObjectId());
        queryBal.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject o : objects) {
                        String bal = String.valueOf(o.getInt("balance")) + "$";
                        String frozenBal = String.valueOf(o.getInt("frozenBalance")) + "$";

                        balanceText.setText(bal + "");
                        frozenBalanceText.setText(frozenBal + "");
                    }
                }
            }
        });

    }

    private void initTablayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("X-Lancer");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logOut) {
            ParseUser user = ParseUser.getCurrentUser();
            Toast.makeText(MainActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
            if (user != null) {
                ParseUser.logOut();
            }
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else if (id == R.id.nav_profile) {
            Toast.makeText(MainActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
//          else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClientFragment(), "Client");
        adapter.addFragment(new PerformerFragment(), "Performer");
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
}
