package com.alisher.work.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alisher.work.activities.MainActivity;
import com.alisher.work.chat.utils.NotificationUtils;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;


public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = CustomPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    private Intent parseIntent;

    public CustomPushReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

            parseIntent = intent;

            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    private void parsePushJson(Context context, JSONObject json) {
        try {
            boolean isBackground = json.getBoolean("is_background");
            boolean isNew = json.getBoolean("isNew");
            boolean isChat = json.getBoolean("isChat");
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");

            if (!isBackground && !isChat) {
                Intent resultIntent = new Intent(context, MainActivity.class);
                showNotificationMessageChat(context, title, message, resultIntent);
            } else if(!isBackground && !isNew){
                Intent resultIntent = new Intent(context, MainActivity.class);
                showNotificationMessage(context, title, message, resultIntent);
            } else {
                Intent resultIntent = new Intent(context, MainActivity.class);
                showNotificationMessage(context, title, message, resultIntent);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);
    }

    private void showNotificationMessageChat(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessageChat(title, message, intent);
    }
}