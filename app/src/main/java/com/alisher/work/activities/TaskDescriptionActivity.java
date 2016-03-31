package com.alisher.work.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class TaskDescriptionActivity extends AppCompatActivity {
    ImageView iv;
    TextView tvN,tvDec,tvCost,tvDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_description);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fabAccept = (FloatingActionButton) findViewById(R.id.finished_fab);
        fabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                Toast.makeText(getApplicationContext(),"Accepted",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TaskDescriptionActivity.this, "Task already accepted", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });

                Intent i = new Intent(TaskDescriptionActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        //iv=(ImageView)findViewById(R.id.img_dec);
        tvN = (TextView) findViewById(R.id.name_dec);
        tvDec=(TextView) findViewById(R.id.dec_dec);
        tvCost=(TextView)findViewById(R.id.cost_dec);
        tvDate=(TextView) findViewById(R.id.deadline_dec);

        tvN.setText(getIntent().getStringExtra("newTaskTitle"));
        tvDec.setText(getIntent().getStringExtra("newTaskDesc"));
        //iv.setImageBitmap((Bitmap) getIntent().getParcelableExtra("newTaskImage"));
        tvCost.setText(getIntent().getStringExtra("newTaskCost")+" $");
        tvDate.setText(getIntent().getStringExtra("newTaskDeadline"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
