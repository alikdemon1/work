package com.alisher.work.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Sergey Kompaniyets on 3/21/2016.
 */
public class PrefManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AndroidHive";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_EMAIL = "email";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}