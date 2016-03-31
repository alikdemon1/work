package com.alisher.work.admin;

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
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.LoginActivity;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Perform;
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
                Perform perf = admins.get(position);
                Intent i = new Intent(AdminActivity.this, ProfileAdmin.class);
                i.putExtra("name", perf.getFirstName() + " " + perf.getLastName());
                i.putExtra("email", perf.getEmail());
                i.putExtra("country", perf.getCountry());
                i.putExtra("city", perf.getCity());
                i.putExtra("street", perf.getStreet());
                i.putExtra("state", perf.getState());
                i.putExtra("ssn", perf.getSsn());
                i.putExtra("buildNo", perf.getBuildNo());
                i.putExtra("zip", perf.getZip());
                startActivity(i);
            }
        }));
        initMap();
    }

    private void initializeData() {
        admins = new ArrayList<>();
        ParseQuery<ParseObject> mainQuery = ParseQuery.getQuery("Achievement");
        mainQuery.whereGreaterThan("performerRating", 0);
        mainQuery.orderByDescending("performerRating");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (final ParseObject p : objects) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("objectId", p.getString("userId"));
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                if (e == null) {
                                    for (ParseUser o : list) {
                                        final Perform perf = new Perform();
                                        perf.setRating((float) p.getDouble("performerRating"));
                                        perf.setId(o.getObjectId());
                                        perf.setFirstName(o.getString("firstName"));
                                        perf.setLastName(o.getString("lastName"));
                                        perf.setCountry(o.getString("country"));
                                        perf.setCity(o.getString("city"));
                                        perf.setState(o.getString("state"));
                                        perf.setZip(o.getString("zip"));
                                        perf.setBuildNo(o.getString("buildingNo"));
                                        perf.setSsn(o.getInt("ssn"));
                                        ParseFile file = o.getParseFile("photo");
                                        try {
                                            Bitmap photo = BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length);
                                            perf.setAvatar(photo);
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        admins.add(perf);
                                    }
                                    ((AdminAdapter) mAdapter).setPerforms(admins);
                                } else {
                                    Toast.makeText(AdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
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
