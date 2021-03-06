package com.alisher.work.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.chat.utils.Const;
import com.alisher.work.chat.utils.IOUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity {

    private ArrayList<Conversation> convList;
    private ChatListAdapter mChatAdapter;
    private TextView tvname;
    private EditText etxtMessage;
    private String buddy;
    private Date lastMsgDate;
    private boolean isRunning;
    private static Handler handler;
    private String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        buddy = getIntent().getStringExtra("firstName");
        getSupportActionBar().setTitle(buddy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        convList = new ArrayList<Conversation>();
        ListView list = (ListView) findViewById(R.id.chatlist);

        mChatAdapter = new ChatListAdapter(ChatActivity.this, UserListActivity.user.getUsername(), convList);
        list.setAdapter(mChatAdapter);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);

        etxtMessage = (EditText) findViewById(R.id.txt);
        etxtMessage.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        setTouchNClick(R.id.btnSend);
        setTouchNClick(R.id.btnAttach);
        extra = getIntent().getStringExtra(Const.EXTRA_DATA);

//        ParseUtils.subscribeWithEmail(ParseUser.getCurrentUser().getUsername());
        handler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.deny) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatActivity.this);
            alertDialog.setTitle("Rejection");
            alertDialog.setMessage("Reject this task?");
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String taskIdDeny = getIntent().getStringExtra("task_id");
                    String s = getIntent().getStringExtra("columnName");
                    Log.d("checkColumn", s + "----" + taskIdDeny);
                    denyMethod(taskIdDeny, s);
                    finish();
                }
            });
            alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setIntent(Intent i, String s) {
        i.putExtra("child", i.getIntExtra("child", 0));
        i.putExtra("group", i.getIntExtra("group", 0));
        i.putExtra("flag", s);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        loadConversationList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnSend) {
            sendMessage();
        } else if (v.getId() == R.id.btnAttach) {
            sendAttach();
        }
    }

    private void sendAttach() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri currImageURI = data.getData();
                Log.d("URI", currImageURI.toString());

                File file = new File(getRealPathFromURI(currImageURI));

                if (file.exists()) {
                    String filepath = file.getAbsolutePath();
                    try {
                        File newFile = new File(filepath);
                        byte[] image = IOUtil.readFile(newFile);
                        ParseFile fileparse = new ParseFile(newFile.getName(), image);
                        fileparse.saveInBackground();
                        ParseObject parseObject = new ParseObject("Task");
                        parseObject.put("attach", fileparse);
                        parseObject.saveInBackground();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("filepath", filepath);
                    getFileURL();
                } else {
                    System.out.println("File Not Found");
                }
            }
        }
    }

    public void getFileURL() {
        ParseQuery<ParseObject> queryParseQuery = ParseQuery.getQuery("Task");
        queryParseQuery.whereEqualTo("objectId", "l3SCbEK17E");
        try {
            List<ParseObject> list = queryParseQuery.find();
            for (ParseObject o : list) {
                ParseFile image = (ParseFile) o.get("attach");
                Log.d("URL", image.getUrl());
                etxtMessage.setText(image.getName() + "" + image.getUrl());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = null;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }

    private void sendMessage() {
        if (etxtMessage.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etxtMessage.getWindowToken(), 0);

        String s = etxtMessage.getText().toString();

        final Conversation c = new Conversation(s, new Date(),
                UserListActivity.user.getUsername());
        c.setStatus(Conversation.STATUS_SENDING);
        convList.add(c);
        mChatAdapter.notifyDataSetChanged();
        etxtMessage.setText(null);

        ParseObject po = new ParseObject("ChatActivity");
        po.put("sender", UserListActivity.user.getUsername());
        po.put("receiver", extra);
        po.put("message", s);
        po.saveEventually(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null)
                    c.setStatus(Conversation.STATUS_SENT);
                else
                    c.setStatus(Conversation.STATUS_FAILED);
                mChatAdapter.notifyDataSetChanged();
            }
        });
        sendNotification(s, ParseUser.getCurrentUser().getString("firstName"), extra);
    }

    private void sendNotification(String message, String title, String email) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", email);

        JSONObject data = null;
        JSONObject main = null;
        try {
            data = new JSONObject();
            main = new JSONObject();
            data.put("message", message);
            data.put("title", title);
            data.put("buddy", extra);
            main.put("data", data);
            main.put("is_background", false);
            main.put("isChat", false);
            main.put("isNew", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON", main.toString());

        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setData(main);
        push.sendInBackground();
    }

    private void moveToRejectionStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "1Ts4KSabKd"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO FINISHED STATUS", e.getMessage());
                }
            }
        });
    }

    public void deleteRejectionTask(final String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (final ParseObject o : list) {
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Task");
                        parseQuery.whereEqualTo("objectId", task_id);
                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : list) {
                                        obj.put("rejectPerfId", o.getString("perfId"));
                                        Log.d("perfID", o.getString("perfId"));
                                        obj.saveEventually();
                                    }
                                } else {
                                    Log.d("ChatActivity", e.getMessage());
                                }
                            }
                        });
                        o.deleteEventually();
                    }
                } else {

                }
            }
        });
    }

    private void denyMethod(final String taskIdDeny, final String s) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Deny");
        query.whereEqualTo("taskId", taskIdDeny);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (!list.isEmpty()) {
                    for (ParseObject o : list) {
                        if (o.getString("clientId") == null) {
                            o.put("clientId", ParseUser.getCurrentUser().getObjectId());
                            o.saveEventually();
                        }
                        if (o.getString("perfId") == null) {
                            o.put("perfId", ParseUser.getCurrentUser().getObjectId());
                            o.saveEventually();
                        }
                        if (o.getString("clientId") != null && o.getString("perfId") != null) {
                            deleteRejectionTask(taskIdDeny);
                            moveToRejectionStatus(taskIdDeny);
                            getBackMoney(o.getString("clientId"));
                            o.deleteInBackground();
                            finish();
                            Toast.makeText(getApplicationContext(), "Task rejected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Waiting...", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    ParseObject p = new ParseObject("Deny");
                    p.put(s, ParseUser.getCurrentUser().getObjectId());
                    p.put("taskId", taskIdDeny);
                    p.saveEventually();
                }
            }
        });
    }

    public void getBackMoney(String clientId) {
        ParseQuery<ParseObject> money = ParseQuery.getQuery("Achievement");
        money.whereEqualTo("userId", clientId);
        money.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject p : objects) {
                        int m = p.getInt("frozenBalance");
                        int back = m + p.getInt("balance");
                        p.put("frozenBalance", 0);
                        p.put("balance", back);
                        p.saveInBackground();
                    }
                } else {

                }
            }
        });
    }


    private void loadConversationList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatActivity");
        if (convList.size() == 0) {
            // load all messages...
            ArrayList<String> al = new ArrayList<String>();
            al.add(extra);
            al.add(UserListActivity.user.getUsername());
            query.whereContainedIn("sender", al);
            query.whereContainedIn("receiver", al);
        } else {
            // load only newly received message..
            if (lastMsgDate != null)
                query.whereGreaterThan("createdAt", lastMsgDate);
            query.whereEqualTo("sender", extra);
            query.whereEqualTo("receiver", UserListActivity.user.getUsername());
        }
        query.orderByDescending("createdAt");
        query.setLimit(50);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, ParseException e) {
                if (li != null && li.size() > 0) {
                    for (int i = li.size() - 1; i >= 0; i--) {
                        ParseObject po = li.get(i);
                        Conversation c = new Conversation(po.getString("message"), po.getCreatedAt(),
                                po.getString("sender"));
                        convList.add(c);
                        if (lastMsgDate == null || lastMsgDate.before(c.getDate()))
                            lastMsgDate = c.getDate();
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning)
                            loadConversationList();
                    }
                }, 1000);
            }
        });
    }

}