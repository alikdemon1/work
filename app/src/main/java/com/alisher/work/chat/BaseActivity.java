package com.alisher.work.chat;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.alisher.work.chat.utils.TouchEffect;


public class BaseActivity extends AppCompatActivity implements OnClickListener {


    public static final TouchEffect TOUCH = new TouchEffect();


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

    }

    public View setTouchNClick(int id) {

        View v = setClick(id);
        if (v != null)
            v.setOnTouchListener(TOUCH);
        return v;
    }

    public View setClick(int id) {

        View v = findViewById(id);
        if (v != null)
            v.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {

    }
}
