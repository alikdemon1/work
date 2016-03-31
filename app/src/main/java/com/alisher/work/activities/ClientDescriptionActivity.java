package com.alisher.work.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.models.Perform;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/23/2016.
 */
public class ClientDescriptionActivity extends AppCompatActivity {

    ImageView iv;
    TextView tvN, tvDec, tvCost, tvDate;
    private Perform perform;
    private String perfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_desc);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("newTaskTitle"));

        //iv = (ImageView) findViewById(R.id.img_desc_client);
        tvN = (TextView) findViewById(R.id.name_desc_client);
        tvDec = (TextView) findViewById(R.id.desc_desc_client);
        tvCost = (TextView) findViewById(R.id.cost_desc_client);
        tvDate = (TextView) findViewById(R.id.deadline_decs_client);

        tvN.setText(getIntent().getStringExtra("newTaskTitle"));
        tvDec.setText(getIntent().getStringExtra("newTaskDesc"));
        //iv.setImageBitmap((Bitmap) getIntent().getParcelableExtra("newTaskImage"));
        tvCost.setText(getIntent().getIntExtra("newTaskCost", 0) + " $");
        tvDate.setText(getIntent().getStringExtra("newTaskDeadline"));
        getSupportActionBar().setTitle(tvN.getText().toString());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.finished_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.finished);
//        boolean isEnabled = getIntent().getBooleanExtra("isEnabled", false);
//        int position = getIntent().getIntExtra("group", 0);
//        if (isEnabled && position == 1) {
//            item.setEnabled(true);
//            item.getIcon().setAlpha(255);
//        } else {
//            item.setEnabled(false);
//            item.getIcon().setAlpha(130);
//        }
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void frozenBalance() {
        ParseQuery<ParseUser> clientQuery = ParseUser.getQuery();
        clientQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        clientQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                for (ParseObject o : list) {
                    int res = o.getInt("frozenBalance") - (getIntent().getIntExtra("newTaskCost", 0));
                    Log.d("COST", getIntent().getIntExtra("newTaskCost", 0) + "");
                    o.put("frozenBalance", res);
                    Log.d("forzenResClient", res + " " + (getIntent().getIntExtra("newTaskCost", 0)));
                    o.saveInBackground();

                    ParseQuery<ParseObject> queryPerfId = ParseQuery.getQuery("Decision");
                    queryPerfId.whereEqualTo("taskId", getIntent().getStringExtra("newTaskId"));
                    queryPerfId.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                for (ParseObject o : list) {
                                    perfId = o.getString("perfId");
                                    Log.d("perfId", perfId);

                                }
                            }
                        }
                    });

                    ParseQuery<ParseUser> perfQuery = ParseUser.getQuery();
                    perfQuery.whereEqualTo("objectId", perfId);
                    perfQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> list, ParseException e) {
                            for (ParseObject perfObj : list) {
                                int taskCost = (getIntent().getIntExtra("newTaskCost", 0));
                                int ress = perfObj.getInt("balance") + taskCost;
                                Log.d("balance", taskCost + " " + perfObj.getInt("balance") + " " + ress);
                                perfObj.put("balance", ress);
                                perfObj.saveInBackground();
                            }
                        }
                    });
                }
            }
        });
    }

    public void vote() {
        final Dialog dialog = new Dialog(ClientDescriptionActivity.this);
        dialog.setContentView(R.layout.vote_dialog);
        dialog.setTitle("Please vote");

        TextView name = (TextView) dialog.findViewById(R.id.vote_name);
        final RatingBar starRate = (RatingBar) dialog.findViewById(R.id.vote_rating);
        starRate.setEnabled(true);
        Button ok = (Button) dialog.findViewById(R.id.vote_ok);
        Button cancel = (Button) dialog.findViewById(R.id.vote_cancel);
        getPerformer(getIntent().getStringExtra("newTaskId"));
        name.setText("Some text will be here");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("Achievement");
                userQuery.whereEqualTo("userId", perform.getId());
                userQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects != null && objects.size() > 0) {
                                ParseObject user = objects.get(0);
                                double a = user.getDouble("performerRating");
                                double total = (a + starRate.getRating()) / 2;
                                Log.d("ASd", a + ", " + perform.getId() + ", " + starRate.getRating() + ", " + total);
                                user.put("performerRating", total);
                                user.saveInBackground();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                frozenBalance();
                deleteFinishedTask(getIntent().getStringExtra("newTaskId"));
                moveToFinishedStatus(getIntent().getStringExtra("newTaskId"));
                setIntent(getIntent());
            }
        });
        dialog.show();
    }

    public void setIntent(Intent i) {
        i.putExtra("child", i.getIntExtra("child", 0));
        i.putExtra("group", i.getIntExtra("group", 0));
        setResult(RESULT_OK, i);
        finish();
    }

    private void moveToFinishedStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "FskciSuqTW"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO FINISHED STATUS", e.getMessage());
                }
            }
        });
    }

    public void deleteFinishedTask(final String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (final ParseObject o : list) {
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Task");
                        parseQuery.whereEqualTo("objectId", task_id);
                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : list) {
                                        obj.put("finishPerfId", o.getString("perfId"));
                                        obj.saveInBackground();
                                    }
                                } else {
                                    Log.d("ChatActivity", e.getMessage());
                                }
                            }
                        });
                        o.deleteEventually();
                    }
                } else {

                }
            }
        });
    }

    public void getPerformer(String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        String perfId = o.getString("perfId");
                        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                        parseQuery.whereEqualTo("objectId", perfId);
                        try {
                            List<ParseUser> users = parseQuery.find();
                            for (ParseUser user : users) {
                                perform = new Perform();
                                perform.setId(user.getObjectId());
                                perform.setFirstName(user.getString("firstName"));
                                perform.setLastName(user.getString("lastName"));
                                perform.setRating((float) user.getDouble("performerRating"));
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}