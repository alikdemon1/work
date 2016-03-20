package com.alisher.work.forTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.MainActivity;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Yesmakhan on 19.03.2016.
 */
public class ResultActivity extends AppCompatActivity{
    ParseUser parseUser;
    String catId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //get rating bar object
        RatingBar bar=(RatingBar)findViewById(R.id.ratingBar1);
        bar.setNumStars(5);
        bar.setStepSize(0.5f);
        //get text view
        TextView t=(TextView)findViewById(R.id.textResult);
        //get score
        Bundle b = getIntent().getExtras();
        int score= b.getInt("score");
        catId=getIntent().getStringExtra("categoryId");
        Log.d("score cat:", score + " " + catId);
        //display score
        bar.setRating(score);
        t.setText("Oopsie! Better Luck Next Time!");

        parseUser = ParseUser.getCurrentUser();
        ParseObject parseObject = new ParseObject("Test");
        parseObject.put("result",score);
        parseObject.put("catId",catId);
        parseObject.put("perfId",parseUser.getObjectId());
        parseObject.saveInBackground();
        Toast.makeText(getApplicationContext(),"result saved",Toast.LENGTH_SHORT).show();
    }

    public void goToMain(View view) {
        Intent i = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(i);
    }
}
