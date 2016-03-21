package com.alisher.work.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class TaskDescriptionActivity extends Activity {
    ImageView iv;
    TextView tvN,tvDec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_description);
        tvN = (TextView) findViewById(R.id.name_dec);
        tvDec=(TextView) findViewById(R.id.dec_dec);
        tvN.setText(getIntent().getStringExtra("newTaskTitle"));
        tvDec.setText(getIntent().getStringExtra("newTaskDesc"));
    }

    public void acceptClicked(View view) {
        Toast.makeText(getApplicationContext(),"Accepted",Toast.LENGTH_SHORT).show();

        ParseObject parseObject = new ParseObject("Decision");
        parseObject.put("taskId",getIntent().getStringExtra("newTaskId"));
        parseObject.put("clientDec",false);
        parseObject.put("perfDec", true);
        parseObject.put("perfId", ParseUser.getCurrentUser().getObjectId());
        parseObject.saveInBackground();

        Intent i = new Intent(TaskDescriptionActivity.this,MainActivity.class);
        startActivity(i);
    }
}
