package com.alisher.work.newtask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.alisher.work.R;
import com.alisher.work.newtask.steppers.PriceFragment;
import com.alisher.work.newtask.steppers.TimeFragment;
import com.alisher.work.newtask.steppers.TitleFragment;

import java.util.ArrayList;
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

        alertDialogBuilder.setTitle("Разместить объявление");
        alertDialogBuilder
                .setMessage("Вы уверены?")
                .setCancelable(true)
                .setPositiveButton("Разместить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        priceText = (EditText) findViewById(R.id.new_price);
                        String name_category = getIntent().getStringExtra("name");
                        int image_category = getIntent().getIntExtra("image", 0);
                        String title = DataHolder.getInstance().getTitle();
                        String time = DataHolder.getInstance().getTime();
                        int price = Integer.parseInt(priceText.getText().toString());
                        String desc = DataHolder.getInstance().getDescription();

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
