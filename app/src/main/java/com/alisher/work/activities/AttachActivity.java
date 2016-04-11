package com.alisher.work.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.adapters.AttachAdapter;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.chat.utils.IOUtil;
import com.alisher.work.models.Attachment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/23/2016.
 */
public class AttachActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    AttachAdapter mAdapter;
    private ArrayList<Attachment> attachments;
    private Button addAttachBtn;
    private String strURL;
    private static final int progress_bar_type = 0;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attachments");

        boolean isVisible = getIntent().getBooleanExtra("isVisible", false);
        addAttachBtn = (Button) findViewById(R.id.attach_btn);

        if (isVisible) {
            addAttachBtn.setEnabled(false);
            addAttachBtn.setVisibility(View.INVISIBLE);
        }

        addAttachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });

        attachments = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.attach_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new AttachAdapter(getApplicationContext(), attachments);
        initializeData();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Attachment catItem = attachments.get(position);
//                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(catItem.getUrl()));
                new DownloadFileFromURL().execute(catItem.getUrl(), catItem.getName());
//                startActivity(browseIntent);
            }
        }));
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
                        ParseObject parseObject = new ParseObject("Result");
                        parseObject.put("taskId", ParseObject.createWithoutData("Task", getIntent().getStringExtra("task_id")));
                        parseObject.put("attach", fileparse);
                        parseObject.put("time", new Date());
                        parseObject.saveInBackground();
                        initializeData();
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

    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
    }

    public void initializeData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Result");
        query.whereEqualTo("taskId", ParseObject.createWithoutData("Task", getIntent().getStringExtra("task_id")));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                attachments.clear();
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        final Attachment aItem = new Attachment();
                        ParseObject o = list.get(i);
                        aItem.setId(o.getObjectId());
                        ParseFile file = (ParseFile) o.get("attach");
                        aItem.setName(file.getName());
                        aItem.setUrl(file.getUrl());
                        aItem.setCreatedAt(o.getCreatedAt());
                        attachments.add(aItem);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(AttachActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void setIntent(Intent i) {
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.arbitration_menu) {
//            moveToArbiterStatus(getIntent().getStringExtra("task_id"));
//            setIntent(getIntent());
        //   }
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

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
                strURL = f_url[1];

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/" + f_url[1]);

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
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/" + strURL;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(imagePath)), "*/*");
            startActivity(intent);
            Log.d("imagePath", imagePath);
        }

    }
}