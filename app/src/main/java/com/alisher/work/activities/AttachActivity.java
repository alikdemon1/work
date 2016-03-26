package com.alisher.work.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.io.IOException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attachments");

        boolean isVisible = getIntent().getBooleanExtra("isVisible", false);
        addAttachBtn = (Button) findViewById(R.id.attach_btn);

        if (!isVisible){
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
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(catItem.getUrl()));
                startActivity(browseIntent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}