package com.alisher.work;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Alisher Kozhabay on 3/18/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this,"zA43rSBXq7yPkOENlWyj0Wl9GEkpV3Wvl4lRSzDf", "pvuwgaK6tFkqJlT6xg0X0Xsphpw6vmU1peWtd53m");
        Parse.enableLocalDatastore(this);
    }
}
