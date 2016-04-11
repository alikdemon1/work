package com.alisher.work.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.chat.utils.Const;
import com.alisher.work.chat.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class UserListActivity extends BaseActivity {

    private ArrayList<ParseUser> uList;
    private TextView textName;
    public static ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        textName = (TextView) findViewById(R.id.textName);
        updateUserStatus(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserList();

    }


    private void updateUserStatus(boolean online) {
        user.put("online", online);
        user.saveEventually();
    }

    private void loadUserList() {
        final ProgressDialog dia = ProgressDialog.show(this, null, getString(R.string.alert_loading));
        String task_id = getIntent().getStringExtra("task_id");
        ParseQuery<ParseObject> decQuery = ParseQuery.getQuery("Decision");
        decQuery.whereEqualTo("taskId", task_id);
        decQuery.whereEqualTo("clientDec", true);
        decQuery.whereEqualTo("perfDec", true);
        List<ParseObject> parseObjects = null;
        try {
            parseObjects = decQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (ParseObject obj : parseObjects) {
            ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
            userParseQuery.whereEqualTo("objectId", obj.getString("perfId"));
            userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    dia.dismiss();
                    if (e == null) {
                        if (list.size() == 0)
                            Toast.makeText(UserListActivity.this, R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                        uList = new ArrayList<ParseUser>(list);
                        ListView l = (ListView) findViewById(R.id.userlist);
                        l.setAdapter(new UserAdapter());
                        l.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int pos, long arg3) {

                                startActivity(new Intent(UserListActivity.this,
                                        ChatActivity.class).putExtra(
                                        Const.EXTRA_DATA, uList.get(pos)
                                                .getUsername()));
                            }
                        });
                    } else {
                        Utils.showDialog(
                                UserListActivity.this,
                                getString(R.string.err_users) + " "
                                        + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class UserAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return uList.size();
        }


        @Override
        public ParseUser getItem(int arg0) {
            return uList.get(arg0);
        }


        @Override
        public long getItemId(int arg0) {
            return arg0;
        }


        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.chat_item, null);

            ParseUser c = getItem(pos);
            TextView lbl = (TextView) v;
            lbl.setText(c.getUsername());

            return v;
        }

    }
}
