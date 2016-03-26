package com.alisher.work.newtask;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.alisher.work.R;
import com.alisher.work.newtask.steppers.PriceFragment;
import com.alisher.work.newtask.steppers.TimeFragment;
import com.alisher.work.newtask.steppers.TitleFragment;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ivb.com.materialstepper.progressMobileStepper;

/**
 * Created by Alisher Kozhabay on 3/6/2016.
 */
public class NewTaskActivity extends progressMobileStepper {

    List<Class> stepperFragmentList = new ArrayList<>();
    private EditText priceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStepperCompleted() {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                NewTaskActivity.this);

        alertDialogBuilder.setTitle("Post task");
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(true)
                .setPositiveButton("post", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        priceText = (EditText) findViewById(R.id.new_price);

                        String name_category = getIntent().getStringExtra("name");
                        String id_category = getIntent().getStringExtra("id");
                        byte[] image_category = getIntent().getByteArrayExtra("image");

                        String title = DataHolder.getInstance().getTitle();
                        String time = DataHolder.getInstance().getTime();
                        int price = Integer.parseInt(priceText.getText().toString());
                        String desc = DataHolder.getInstance().getDescription();

                        Date endTime = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(endTime);
                        c.add(Calendar.DATE, DataHolder.getInstance().getDay());
                        c.add(Calendar.HOUR_OF_DAY, DataHolder.getInstance().getHours());
                        c.add(Calendar.MINUTE, DataHolder.getInstance().getMinutes());
                        endTime = c.getTime();

                        ParseFile file = new ParseFile("task_logo.png", image_category);
                        file.saveInBackground();
                        ParseObject task = new ParseObject("Task");
                        task.put("name", title);
                        task.put("catId", ParseObject.createWithoutData("Category", id_category));
                        task.put("cost", price);
                        task.put("img", file);
                        task.put("clientId", ParseObject.createWithoutData(ParseUser.class, ParseUser.getCurrentUser().getObjectId()));
                        task.put("description", desc);
                        task.put("statusId", ParseObject.createWithoutData("Status", "vVMYOEUIeY"));
                        task.put("duration", Arrays.asList(DataHolder.getInstance().getDay(), DataHolder.getInstance().getHours(), DataHolder.getInstance().getMinutes()));
                        task.put("startTime", new Date());
                        task.put("endTime", endTime);
                        task.saveInBackground();
                        //task.put("attach", "Some file here");

                        Intent intent = getIntent();
                        intent.putExtra("name_category", name_category);
                        intent.putExtra("title", title);
                        intent.putExtra("time", time);
                        intent.putExtra("image_category", image_category);
                        intent.putExtra("price", price);
                        intent.putExtra("desc", desc);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public List<Class> init() {
        stepperFragmentList.add(TitleFragment.class);
        stepperFragmentList.add(TimeFragment.class);
        stepperFragmentList.add(PriceFragment.class);

        return stepperFragmentList;
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
