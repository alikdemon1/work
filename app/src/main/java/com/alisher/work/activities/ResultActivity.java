package com.alisher.work.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Kompaniyets on 3/29/2016.
 */
public class ResultActivity extends AppCompatActivity {
    private Button addAttachBtn;
    private String task_id;
    private TextView resultTxt, textResult;
    private String urlFile;
    private Perform perform;
    private String perfId;
    private Button attachBtn;
    private Button sendBtn;
    private EditText editText;
    private String nameFile;
    private static final int progress_bar_type = 0;
    private ProgressDialog pDialog;

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
        editText = (EditText) findViewById(R.id.TextTxt);
        textResult = (TextView) findViewById(R.id.text_result);

        getPerformer(task_id);

        boolean isVisible = getIntent().getBooleanExtra("isVisible", false);

        if (!isVisible) {
            attachBtn.setEnabled(false);
            attachBtn.setVisibility(View.INVISIBLE);

            editText.setEnabled(false);
            editText.setVisibility(View.INVISIBLE);

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
                if (editText.length() == 0) {
                    Toast.makeText(ResultActivity.this, "Please enter message", Toast.LENGTH_SHORT);
                } else {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
                    alertDialog.setTitle("Send");
                    alertDialog.setMessage("Send for check?");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                                                            obj.put("textForResult", editText.getText().toString());
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
                            sendNotification(editText.getText().toString());
                            setIntent(getIntent());
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
            }
        });
        getFileForTask(task_id);
    }

    private void sendNotification(final String message) {
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
                            data.put("message", message);
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
                                            @TargetApi(Build.VERSION_CODES.M)
                                            @Override
                                            public void done(ParseException e) {
                                                Log.d("SUCCESS", "SUCCESS");
                                                sendBtn.setEnabled(true);
                                                sendBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
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
                        String text = p.getString("textForResult");
                        if (text == null) {

                        } else {
                            textResult.setText(text + " ");
                        }
                        if (file == null) {
                        } else {
                            String result = file.getName().substring(file.getName().lastIndexOf('-') + 1).trim();
                            urlFile = file.getUrl();
                            nameFile = file.getName();
                            resultTxt.setText(result);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
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
            new DownloadFile().execute(urlFile);
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
        dialog.setTitle("Rating");

        TextView name = (TextView) dialog.findViewById(R.id.vote_name);
        final RatingBar starRate = (RatingBar) dialog.findViewById(R.id.vote_rating);
        starRate.setEnabled(true);
        Button ok = (Button) dialog.findViewById(R.id.vote_ok);
        Button cancel = (Button) dialog.findViewById(R.id.vote_cancel);
        name.setText("Please rate the performer");
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
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
                query.whereEqualTo("objectId", task_id);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            for (ParseObject p : objects) {
                                sendNotification(p.getString("description"), "Task finished " + p.getString("name"), perform.getEmail());
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
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

    public void getPerformer(final String task_id) {
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
                                perform.setEmail(user.getUsername());
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void sendNotification(String message, String title, String rList) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", rList);
        JSONObject data = null;
        JSONObject main = null;
        try {
            data = new JSONObject();
            main = new JSONObject();
            data.put("message", message);
            data.put("title", title);
            main.put("data", data);
            main.put("is_background", false);
            main.put("isNew", false);
            main.put("isChat", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON", main.toString());
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setData(main);
        push.sendInBackground();
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

    public void sendNotificationArbiter() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject p : objects) {
                        Log.d("PERFORM_ID", perform.getEmail());
                        sendNotification(p.getString("description"), "Task move to arbiter " + p.getString("name"), perform.getEmail());
                    }
                } else {
                    e.printStackTrace();
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
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
            alertDialog.setTitle("Arbiter");
            alertDialog.setMessage("Move task to arbiter status?");
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveToArbiterStatus(task_id);
                    deleteArbitrageTask(task_id);
                    sendNotificationArbiter();
                    setIntent(getIntent());
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
        } else if (id == R.id.check_finish) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
            alertDialog.setTitle("Finish");
            alertDialog.setMessage("Move task to finish status?");
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    vote();
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
        } else if (id == R.id.check_modify) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
            alertDialog.setTitle("Return to work status");
            alertDialog.setMessage("Return task to work status?");
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    returnToWorkStatus(task_id);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
                    query.whereEqualTo("objectId", task_id);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject p : objects) {
                                    Log.d("PERFORM_ID", perform.getEmail());
                                    sendNotification(p.getString("description"), "Task returned to work " + p.getString("name"), perform.getEmail());
                                }
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                    setIntent(getIntent());
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    class DownloadFile extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/" + nameFile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }


        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/" + nameFile;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(imagePath)), "*/*");
            startActivity(intent);
            Log.d("imagePath", imagePath);
        }

    }
}

