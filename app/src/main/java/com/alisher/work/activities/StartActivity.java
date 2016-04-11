package com.alisher.work.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.alisher.work.arbitor.ArbitorActivity;
import com.alisher.work.arbitor.ListArbitorActivity;
import com.alisher.work.admin.AdminActivity;
import com.alisher.work.admin.AdminAdapter;
import com.alisher.work.admin.MapAdminActivity;
import com.alisher.work.chat.UserListActivity;
import com.alisher.work.chat.utils.ParseUtils;
import com.parse.ParseUser;


/**
 * Created by Sergey Kompaniyets on 3/18/2016.
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
                    ParseUtils.subscribeWithEmail(currentUser.getUsername());
                    finish();
                } else {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }
}