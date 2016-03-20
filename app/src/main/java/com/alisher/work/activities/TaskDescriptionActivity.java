package com.alisher.work.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.models.Task;

public class TaskDescriptionActivity extends Activity {
    ImageView iv;
    TextView tvN,tvDec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_description);
        Task task = (Task) getIntent().getSerializableExtra("newTask");
        tvN.setText(task.getTitle());
        tvDec.setText(task.getDesc());
        iv.setImageBitmap(task.getImage());
    }

    public void acceptClicked(View view) {
        Toast.makeText(getApplicationContext(),"Accepted",Toast.LENGTH_SHORT).show();

        Intent i = new Intent(TaskDescriptionActivity.this,MainActivity.class);
        startActivity(i);
    }
}
