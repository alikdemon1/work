package com.alisher.work.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.adapters.PerformsForEachTaskAdapter;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Perform;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/13/2016.
 */
public class PerformsForEachTaskActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Perform> pts;
    private float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pt);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.pt_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new PerformsForEachTaskAdapter(getApplicationContext());
        Intent i = getIntent();
        try {
            initializeData(i.getStringExtra("taskId"));
            Log.d("TASK_ID", i.getStringExtra("taskId"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Dialog dialog = new Dialog(PerformsForEachTaskActivity.this);
                dialog.setContentView(R.layout.select_dialog);
                dialog.setTitle("Perform details");

                TextView name = (TextView) dialog.findViewById(R.id.movie_name);
                TextView starRate = (TextView) dialog.findViewById(R.id.rate);
                Button ok = (Button) dialog.findViewById(R.id.ok_btn);
                Button cancel = (Button) dialog.findViewById(R.id.cancel_btn);

                final Perform perform = pts.get(position);
                name.setText("Perfrom name: " + perform.getFirstName());
                starRate.setText("Perform rating: " + perform.getRating());

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosedPerformer(perform.getId());
                        Intent i = getIntent();
                        moveToWorkStatus(i.getStringExtra("taskId"));
                        i.putExtra("child", i.getIntExtra("child_position", 0));
                        i.putExtra("group", i.getIntExtra("group_position", 0));

                        frozenBalance();
                        setResult(RESULT_OK, i);
                        finish();
                    }
                });
                dialog.show();
            }
        }));
    }

    private void frozenBalance() {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Achievement");
        parseQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    int price = getIntent().getIntExtra("taskPriceForBalance", 0);
                    int bal = object.getInt("balance") - price;
                    int fbalance = object.getInt("frozenBalance") + price;
                    object.put("balance", bal);
                    object.put("frozenBalance", fbalance);
                    object.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void choosedPerformer(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("perfId", id);
        query.whereEqualTo("taskId", getIntent().getStringExtra("taskId"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("clientDec", true);
                        o.saveInBackground();
                    }
                } else {
                    Log.d("CHOOSED PERFORMER", e.getMessage());
                }
            }
        });
    }

    public void moveToWorkStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "j6hNwQ01bt"));
                        o.put("isStarted", true);
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO WORK STATUS", e.getMessage());
                }
            }
        });
    }

    public void initializeData(String taskId) throws ParseException {
        pts = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", taskId);
        List<ParseObject> performs = query.find();
        for (ParseObject obj : performs) {
            ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
            userParseQuery.whereEqualTo("objectId", obj.getString("perfId"));
            final List<ParseUser> list = userParseQuery.find();
            Log.d("LIST", list.size() + "");
            for (final ParseUser o : list) {
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Achievement");
                parseQuery.whereEqualTo("userId", o.getObjectId());
                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            for (ParseObject p : objects) {
                                final Perform perf = new Perform();
                                perf.setId(o.getObjectId());
                                perf.setFirstName(o.getString("firstName"));
                                perf.setLastName(o.getString("lastName"));
                                rating = (float) p.getDouble("performerRating");
                                perf.setRating(rating);
                                ParseFile file = o.getParseFile("photo");
                                Bitmap photo = null;
                                try {
                                    photo = BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length);
                                    perf.setAvatar(photo);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                pts.add(perf);
                            }
                            ((PerformsForEachTaskAdapter) mAdapter).setPerformsTask(pts);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private void getRatingForPerf(ParseUser parseUser, final Perform perf) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}