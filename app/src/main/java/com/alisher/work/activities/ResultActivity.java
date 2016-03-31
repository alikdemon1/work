package com.alisher.work.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.chat.utils.IOUtil;
import com.alisher.work.models.Perform;
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
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/29/2016.
 */
public class ResultActivity extends AppCompatActivity {
    private Button addAttachBtn;
    private String task_id;
    private TextView resultTxt;
    private String urlFile;
    private Perform perform;
    private String perfId;
    private Button attachBtn;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Result");

        task_id = getIntent().getStringExtra("task_id");
        attachBtn = (Button) findViewById(R.id.result_attach);
        sendBtn = (Button) findViewById(R.id.result_finish);
        resultTxt = (TextView) findViewById(R.id.name_result);
        boolean isVisible = getIntent().getBooleanExtra("isVisible", false);

        if (!isVisible) {
            attachBtn.setEnabled(false);
            attachBtn.setVisibility(View.INVISIBLE);

            sendBtn.setEnabled(false);
            sendBtn.setVisibility(View.INVISIBLE);
        }

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToInCheckStatus(task_id);
                ParseQuery<ParseObject> queryCheck = ParseQuery.getQuery("Decision");
                queryCheck.whereEqualTo("taskId", task_id);
                queryCheck.findInBackground(new FindCallback<ParseObject>() {
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
                                                obj.put("checkPerfId", o.getString("perfId"));
                                                obj.saveInBackground();
                                            }
                                        } else {
                                            Log.d("ChatActivity", e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                });
                sendNotification();
                setIntent(getIntent());
                finish();
            }
        });
        getFileForTask(task_id);
    }

    private void sendNotification() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", task_id);
        query.include("clientId");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject p : objects) {
                        ParseUser user = (ParseUser) p.getParseObject("clientId");
                        ParseQuery pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereEqualTo("email", user.getUsername());
                        JSONObject data = null;
                        JSONObject main = null;
                        try {
                            data = new JSONObject();
                            main = new JSONObject();
                            data.put("message", "Please check your task");
                            data.put("title", p.getString("name") + " done");
                            main.put("data", data);
                            main.put("is_background", false);
                            main.put("isNew", false);
                            main.put("isChat", true);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        Log.d("JSON", main.toString());
                        ParsePush push = new ParsePush();
                        push.setQuery(pushQuery);
                        push.setData(main);
                        push.sendInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
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
                        final ParseFile fileparse = new ParseFile(newFile.getName(), image);
                        fileparse.saveInBackground();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
                        query.whereEqualTo("objectId", task_id);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject p : objects) {
                                        p.put("attach", fileparse);
                                        p.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Log.d("SUCCESS", "SUCCESS");
                                                sendBtn.setEnabled(true);
                                            }
                                        });
                                    }
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("filepath", filepath);
                } else {
                    System.out.println("File Not Found");
                }
            }
        }
    }

    public void getFileForTask(String taskId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", taskId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject p : objects) {
                        ParseFile file = (ParseFile) p.get("attach");
                        if (file == null) {

                        } else {
                            String result = file.getName().substring(file.getName().lastIndexOf('-') + 1).trim();
                            urlFile = file.getUrl();
                            resultTxt.setText(result);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri,
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void moveToInCheckStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "o2bnAc0fEy"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVETOCHECK STATUS", e.getMessage());
                }
            }
        });
    }

    public void setIntent(Intent i) {
        setResult(RESULT_OK, i);
        finish();
    }

    public void uploadFile(View view) {
        if (urlFile != null) {
            Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFile));
            startActivity(browseIntent);
        } else {
            Toast.makeText(ResultActivity.this, "Nothing to download", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveToArbiterStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "Y5lhU6qfgB"));
                        o.put("arbitragePerfId", ParseUser.getCurrentUser().getObjectId());
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVETARBITRATION STATUS", e.getMessage());
                }
            }
        });
    }

    private void moveToFinishedStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "FskciSuqTW"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO FINISHED STATUS", e.getMessage());
                }
            }
        });
    }

    public void vote() {
        final Dialog dialog = new Dialog(ResultActivity.this);
        dialog.setContentView(R.layout.vote_dialog);
        dialog.setTitle("Please vote");

        TextView name = (TextView) dialog.findViewById(R.id.vote_name);
        final RatingBar starRate = (RatingBar) dialog.findViewById(R.id.vote_rating);
        starRate.setEnabled(true);
        Button ok = (Button) dialog.findViewById(R.id.vote_ok);
        Button cancel = (Button) dialog.findViewById(R.id.vote_cancel);
        getPerformer(task_id);
        name.setText("Some text will be here");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frozenBalance();
                setRating(starRate);
                moveToFinishedStatus(task_id);
                deleteFinishedTask(task_id);
                setIntent(getIntent());
            }
        });
        dialog.show();
    }

    private void setRating(final RatingBar starRate) {
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("Achievement");
        userQuery.whereEqualTo("userId", perform.getId());
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects != null && objects.size() > 0) {
                        ParseObject user = objects.get(0);
                        double sum = starRate.getRating() + user.getDouble("sum");
                        int count = user.getInt("count") + 1;
                        double total = sum / count;
                        Log.d("ASd", sum + ", " + starRate.getRating() + ", " + count);
                        user.put("sum", sum);
                        user.put("sum", sum);
                        user.put("count", count);
                        user.put("performerRating", total);
                        user.saveInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void frozenBalance() {
        ParseQuery<ParseObject> clientQuery = ParseQuery.getQuery("Achievement");
        clientQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        clientQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject o : objects) {
                    int res = o.getInt("frozenBalance") - (getIntent().getIntExtra("newTaskCost", 0));
                    Log.d("COST", getIntent().getIntExtra("newTaskCost", 0) + "");
                    o.put("frozenBalance", res);
                    Log.d("forzenResClient", res + " " + (getIntent().getIntExtra("newTaskCost", 0)));

                    ParseQuery<ParseObject> queryPerfId = ParseQuery.getQuery("Decision");
                    queryPerfId.whereEqualTo("taskId", task_id);
                    queryPerfId.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                for (ParseObject o : list) {
                                    perfId = o.getString("perfId");
                                    Log.d("perfId", perfId);
                                    ParseQuery<ParseObject> perfQuery = ParseQuery.getQuery("Achievement");
                                    perfQuery.whereEqualTo("userId", perfId);
                                    perfQuery.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            for (ParseObject perfObj : objects) {
                                                int taskCost = (getIntent().getIntExtra("newTaskCost", 0));
                                                int ress = perfObj.getInt("balance") + taskCost;
                                                Log.d("balance", taskCost + " " + perfObj.getInt("balance") + " " + ress);
                                                perfObj.put("balance", ress);
                                                perfObj.saveInBackground();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                    o.saveInBackground();
                }
            }
        });
    }

    public void deleteFinishedTask(final String task_id) {
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
                                        obj.put("finishPerfId", o.getString("perfId"));
                                        obj.saveInBackground();
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

    public void deleteArbitrageTask(final String task_id) {
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
                                        obj.put("arbitragePerfId", o.getString("perfId"));
                                        obj.saveInBackground();
                                    }
                                } else {
                                    Log.d("ChatActivity", e.getMessage());
                                }
                            }
                        });
                    }
                } else {

                }
            }
        });
    }

    public void getPerformer(String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        String perfId = o.getString("perfId");
                        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                        parseQuery.whereEqualTo("objectId", perfId);
                        try {
                            List<ParseUser> users = parseQuery.find();
                            for (ParseUser user : users) {
                                perform = new Perform();
                                perform.setId(user.getObjectId());
                                perform.setFirstName(user.getString("firstName"));
                                perform.setLastName(user.getString("lastName"));
                                perform.setRating((float) user.getDouble("performerRating"));
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void returnToWorkStatus(String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "j6hNwQ01bt"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO FINISHED STATUS", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.check_arbiter) {
            moveToArbiterStatus(task_id);
            deleteArbitrageTask(task_id);
            setIntent(getIntent());
        } else if (id == R.id.check_finish) {
            vote();
        } else if (id == R.id.check_modify) {
            returnToWorkStatus(task_id);
            setIntent(getIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem arbiter = menu.findItem(R.id.check_arbiter);
        MenuItem finished = menu.findItem(R.id.check_finish);
        MenuItem modify = menu.findItem(R.id.check_modify);

        boolean isEnabled = getIntent().getBooleanExtra("isAvailable", false);

        if (isEnabled) {
            arbiter.setVisible(true);
            finished.setVisible(true);
            modify.setVisible(true);
        } else {
            arbiter.setVisible(false);
            finished.setVisible(false);
            modify.setVisible(false);
        }
        return true;
    }
}