package com.alisher.work.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sergey Kompaniyets on 3/23/2016.
 */
public class ClientDescriptionActivity extends AppCompatActivity {

    ImageView iv;
    TextView tvN, tvDec, tvCost, tvDate, tvCategory;
    private Perform perform;
    private String perfId;
    private String catName;
    private FloatingActionButton cancelTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_desc);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("newTaskTitle"));

        cancelTask = (FloatingActionButton) findViewById(R.id.fab_cancel_task);

        boolean isVisible = getIntent().getBooleanExtra("isVisibleCancel", false);
        int groupPos = getIntent().getIntExtra("group", 0);

        if (isVisible && groupPos == 0){
            Log.d("FAB", "TRUE");
        } else {
            Log.d("FAB", "FALSE");
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) cancelTask.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            cancelTask.setLayoutParams(p);
            cancelTask.setVisibility(View.GONE);
        }

        cancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
                query.whereEqualTo("objectId", getIntent().getStringExtra("newTaskId"));
                try {
                    List<ParseObject> objects = query.find();
                    for (ParseObject p : objects) {
                        p.put("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
                        p.saveEventually();
                        Toast.makeText(ClientDescriptionActivity.this, "Task moved to draft", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //iv = (ImageView) findViewById(R.id.img_desc_client);
        tvN = (TextView) findViewById(R.id.name_desc_client);
        tvDec = (TextView) findViewById(R.id.desc_desc_client);
        tvCost = (TextView) findViewById(R.id.cost_desc_client);
        tvDate = (TextView) findViewById(R.id.deadline_decs_client);
        tvCategory = (TextView) findViewById(R.id.category_desc_client);

        getCategoryName();

        tvN.setText(getIntent().getStringExtra("newTaskTitle"));
        tvDec.setText(getIntent().getStringExtra("newTaskDesc"));
        //iv.setImageBitmap((Bitmap) getIntent().getParcelableExtra("newTaskImage"));
        tvCost.setText(getIntent().getIntExtra("newTaskCost", 0) + " $");
        tvDate.setText(getIntent().getStringExtra("newTaskDeadline"));
        getSupportActionBar().setTitle(tvN.getText().toString());
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
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setIntent(Intent i) {
        i.putExtra("child", i.getIntExtra("child", 0));
        i.putExtra("group", i.getIntExtra("group", 0));
        setResult(RESULT_OK, i);
        finish();
    }
}