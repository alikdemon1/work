package com.alisher.work.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisher.work.R;

/**
 * Created by Alisher Kozhabay on 3/23/2016.
 */
public class ClientDescriptionActivity extends AppCompatActivity {

    ImageView iv;
    TextView tvN,tvDec,tvCost,tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_desc);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv=(ImageView)findViewById(R.id.img_desc_client);
        tvN = (TextView) findViewById(R.id.name_desc_client);
        tvDec=(TextView) findViewById(R.id.description_desc_client);
        tvCost=(TextView)findViewById(R.id.cost_desc_client);
        tvDate=(TextView) findViewById(R.id.deadline_desc_client);

        tvN.setText(getIntent().getStringExtra("newTaskTitle"));
        tvDec.setText(getIntent().getStringExtra("newTaskDesc"));
        iv.setImageBitmap((Bitmap) getIntent().getParcelableExtra("newTaskImage"));
        tvCost.setText(getIntent().getStringExtra("newTaskCost")+" $");
        tvDate.setText(getIntent().getStringExtra("newTaskDeadline"));
        getSupportActionBar().setTitle(tvN.getText().toString());
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