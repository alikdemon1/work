package com.alisher.work.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
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
import com.alisher.work.activities.LoginActivity;
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
import com.parse.PushService;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity {
    /**
     * The Conversation list.
     */
    private ArrayList<Conversation> convList;
    //private ArrayList<ParseObject> msgList;

    /**
     * The chat_layout adapter.
     */
    private ChatListAdapter mChatAdapter;

    private TextView tvname;
    /**
     * The Editext to compose the message.
     */
    private EditText etxtMessage;

    /**
     * The user name of buddy.
     */
    private String buddy;

    /**
     * The date of last message in conversation.
     */
    private Date lastMsgDate;

    /**
     * Flag to hold if the activity is running or not.
     */
    private boolean isRunning;

    /**
     * The handler.
     */
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        initToolbar();
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

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        tvname = (TextView) findViewById(R.id.tvName);
        tvname.setText(buddy);
        handler = new Handler();
    }

    public void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.arbitration) {
            Toast.makeText(ChatActivity.this, "Arbitration coming soon...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.finished) {
            Intent i = getIntent();
            setIntent(i, "finished");
            deleteFinishedTask(i.getStringExtra("task_id"));
        }
        return super.onOptionsItemSelected(item);
    }

    public void setIntent(Intent i, String s){
        i.putExtra("child", i.getIntExtra("child", 0));
        i.putExtra("group", i.getIntExtra("group", 0));
        i.putExtra("flag", s);
        setResult(RESULT_OK, i);
        finish();
    }

    public void deleteFinishedTask(String task_id){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.deleteEventually();
                    }
                } else {

                }
            }
        });
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
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri currImageURI = data.getData();
                Log.d("URI", currImageURI.toString());

                File file = new File(getRealPathFromURI(currImageURI));

                if (file.exists()) {
                    String filepath=file.getAbsolutePath();
                    try {
                        File newFile = new File(filepath);
                        byte[] image = IOUtil.readFile(newFile);
                        ParseFile fileparse = new ParseFile("example", image);
                        fileparse.saveInBackground();
                        ParseObject parseObject = new ParseObject("Task");
                        parseObject.put("attach", fileparse);
                        parseObject.saveInBackground();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("filepath", filepath);
                }
                else
                {
                    System.out.println("File Not Found");
                }
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri)
    {
        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    /**
     * Call this method to Send message to opponent. It does nothing if the text
     * is empty otherwise it creates a Parse object for ChatActivity message and send it
     * to Parse server.
     */
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
        po.put("receiver", buddy);
        // po.put("createdAt", "");
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
    }

    /**
     * Load the conversation list from Parse server and save the date of last
     * message that will be used to load only recent new messages
     */
    private void loadConversationList() {
        //	ParseQuery<ParseObject> senderquery = ParseQuery.getQuery("ChatActivity");
        //	ParseQuery<ParseObject> receivequery = ParseQuery.getQuery("ChatActivity");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatActivity");
        if (convList.size() == 0) {
            // load all messages...
            ArrayList<String> al = new ArrayList<String>();
            al.add(buddy);
            al.add(UserListActivity.user.getUsername());
            query.whereContainedIn("sender", al);
            query.whereContainedIn("receiver", al);
        } else {
            // load only newly received message..
            if (lastMsgDate != null)
                query.whereGreaterThan("createdAt", lastMsgDate);
            query.whereEqualTo("sender", buddy);
            query.whereEqualTo("receiver", UserListActivity.user.getUsername());
        }
        //	if (lastMsgDate != null)
        //		query.whereGreaterThan("createdAt", lastMsgDate);
        query.orderByDescending("createdAt");
        query.setLimit(50);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, ParseException e) {
                if (li != null && li.size() > 0) {
                    Log.d("Faheem", "li size" + li.size());
                    for (int i = li.size() - 1; i >= 0; i--) {
                        ParseObject po = li.get(i);
                        Conversation c = new Conversation(po.getString("message"), po.getCreatedAt(),
                                po.getString("sender"));
                        convList.add(c);

                        Log.d("Faheem", "li size" + po.getString("message") + " " + po.getString("sender") + "");

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


    private void logout() {
        //UserListActivity.user.g
        ParseUser.getCurrentUser().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
