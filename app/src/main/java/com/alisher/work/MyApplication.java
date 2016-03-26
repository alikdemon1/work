package com.alisher.work;

import android.app.Application;

import com.alisher.work.chat.utils.ParseUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Alisher Kozhabay on 3/18/2016.
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // register with parse
        ParseUtils.registerParse(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}
