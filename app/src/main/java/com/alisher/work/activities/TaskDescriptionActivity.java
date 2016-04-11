package com.alisher.work.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskDescriptionActivity extends AppCompatActivity {
    ImageView iv;
    TextView tvN, tvDec, tvCost, tvDate, tvCategory;
    private String catName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_description);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fabAccept = (FloatingActionButton) findViewById(R.id.finished_fab);
        fabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskDescriptionActivity.this);
                alertDialog.setTitle("Accept");
                alertDialog.setMessage("Accept this task?");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
                        query.whereEqualTo("taskId", getIntent().getStringExtra("newTaskId"));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.isEmpty()) {
                                        ParseObject parseObject = new ParseObject("Decision");
                                        parseObject.put("taskId", getIntent().getStringExtra("newTaskId"));
                                        parseObject.put("clientDec", false);
                                        parseObject.put("perfDec", true);
                                        parseObject.put("perfId", ParseUser.getCurrentUser().getObjectId());
                                        parseObject.saveInBackground();
                                        Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_SHORT).show();

                                        ParseQuery<ParseObject> catQuery = ParseQuery.getQuery("Task");
                                        catQuery.whereEqualTo("objectId", getIntent().getStringExtra("newTaskId"));
                                        catQuery.include("clientId");
                                        catQuery.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {
                                                if (e == null) {
                                                    for (ParseObject p : objects) {
                                                        ParseUser obj = (ParseUser) p.getParseObject("clientId");
                                                        Log.d("Email Client", obj.getUsername());
                                                        sendNotification(obj.getUsername());
                                                    }
                                                } else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(TaskDescriptionActivity.this, "Task already accepted", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                        finish();
                    }
                });
                alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        //iv=(ImageView)findViewById(R.id.img_dec);
        tvN = (TextView) findViewById(R.id.name_dec);
        tvDec = (TextView) findViewById(R.id.dec_dec);
        tvCost = (TextView) findViewById(R.id.cost_dec);
        tvDate = (TextView) findViewById(R.id.deadline_dec);
        tvCategory = (TextView) findViewById(R.id.category_dec);

        getCategoryName();
        tvN.setText(getIntent().getStringExtra("newTaskTitle"));
        tvDec.setText(getIntent().getStringExtra("newTaskDesc"));
        //iv.setImageBitmap((Bitmap) getIntent().getParcelableExtra("newTaskImage"));
        tvCost.setText(getIntent().getStringExtra("newTaskCost") + " $");
        tvDate.setText(getIntent().getStringExtra("newTaskDeadline"));
    }

    private void sendNotification(String rList) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", rList);
        JSONObject data = null;
        JSONObject main = null;
        try {
            data = new JSONObject();
            main = new JSONObject();
            data.put("message", "check");
            data.put("title", "Task was accepted by " + ParseUser.getCurrentUser().getString("firstName"));
            main.put("data", data);
            main.put("is_background", false);
            main.put("isNew", false);
            main.put("isChat", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON", main.toString());
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setData(main);
        push.sendInBackground();
    }

    public void getCategoryName(){
        ParseQuery<ParseObject> catQuery = ParseQuery.getQuery("Task");
        catQuery.whereEqualTo("objectId", getIntent().getStringExtra("newTaskId"));
        catQuery.include("catId");
        catQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject p : objects) {
                        ParseObject obj = p.getParseObject("catId");
                        catName = obj.getString("name");
                        Log.d("CAT_NAME", catName);
                        tvCategory.setText(catName);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
