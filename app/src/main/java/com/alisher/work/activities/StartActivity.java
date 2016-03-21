package com.alisher.work.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.alisher.work.chat.UserListActivity;
import com.parse.ParseUser;


/**
 * Created by Alisher Kozhabay on 3/18/2016.
 */
public class StartActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;
    private ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    UserListActivity.user = currentUser;
                    finish();
                } else {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }
}