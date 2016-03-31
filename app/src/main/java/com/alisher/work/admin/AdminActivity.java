package com.alisher.work.admin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.LoginActivity;
import com.alisher.work.adapters.PerformsForEachTaskAdapter;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Perform;
import com.alisher.work.newtask.CategoryActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/26/2016.
 */
public class AdminActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Perform> admins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.admin_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new AdminAdapter(getApplicationContext());
        initializeData();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("AdminActivity", "Clicked performer in position = " + position);
            }
        }));
        initMap();
    }

    private void initializeData() {
        admins = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Achievement");
        parseQuery.whereGreaterThan("performerRating", 0);
        parseQuery.orderByDescending("performerRating");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (final ParseObject o : objects) {
                        final Perform perf = new Perform();
                        perf.setId(o.getString("userId"));
                        perf.setRating((float) o.getDouble("performerRating"));

                        ParseQuery<ParseUser> userQ= ParseUser.getQuery();
                        userQ.whereEqualTo("objectId", o.getString("userId"));
                        userQ.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                for (ParseUser user : objects) {
                                    perf.setFirstName(user.getString("firstName"));
                                    perf.setLastName(user.getString("lastName"));
                                    ParseFile file = user.getParseFile("photo");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if (e == null){
                                                Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                perf.setAvatar(photo);
                                            } else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        admins.add(perf);
                    }
                    ((AdminAdapter) mAdapter).setPerforms(admins);
                } else {
                    Toast.makeText(AdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initMap() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.openMap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MapAdminActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exit_admin) {
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
