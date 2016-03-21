package com.alisher.work.chat;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.alisher.work.chat.utils.TouchEffect;


public class BaseActivity extends FragmentActivity implements OnClickListener {

    /**
     * Apply this Constant as touch listener for views to provide alpha touch
     * effect. The view must have a Non-Transparent background.
     */
    public static final TouchEffect TOUCH = new TouchEffect();


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

    }

    /**
     * Sets the touch and click listener for a view with given id.
     *
     * @param id the id
     * @return the view on which listeners applied
     */
    public View setTouchNClick(int id) {

        View v = setClick(id);
        if (v != null)
            v.setOnTouchListener(TOUCH);
        return v;
    }

    /**
     * Sets the click listener for a view with given id.
     *
     * @param id the id
     * @return the view on which listener is applied
     */
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
