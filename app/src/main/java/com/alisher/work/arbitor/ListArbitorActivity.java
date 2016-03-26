package com.alisher.work.arbitor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.TaskDescriptionActivity;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yesmakhan on 25.03.2016.
 */
public class ListArbitorActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Task> tasks;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_arbitor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_ar_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new ArbitorRecyclerAdapter(getApplicationContext());
        initializeData();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Task newTask = tasks.get(position);
                Log.d("ARBITOR", position + ", " + newTask.toString());

                Intent i = new Intent(ListArbitorActivity.this, ArbitorActivity.class);
                i.putExtra("newTaskTitle", newTask.getTitle() + "");
                i.putExtra("newTaskDesc", newTask.getDesc() + "");
                i.putExtra("newTaskId", newTask.getId() + "");
                i.putExtra("newTaskImage",newTask.getImage());
                i.putExtra("newTaskClientId",newTask.getCatId());
                i.putExtra("newTaskCost",String.valueOf(newTask.getPrice()));
                long mili = newTask.getEndTime().getTime();
                i.putExtra("newTaskEndtime",mili);
                Log.d("task id",newTask.getId());
                startActivity(i);
            }
        }));
    }

    public String getMyData() {
        return "HELLO";
    }

    private void initializeData() {
        tasks = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("statusId", ParseObject.createWithoutData("Status", "Y5lhU6qfgB"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        final Task taskItem = new Task();
                        ParseObject o = list.get(i);

                        taskItem.setId(o.getObjectId());
                        taskItem.setTitle(o.getString("name"));
                        taskItem.setDesc(o.getString("description"));
                        taskItem.setDuration(o.getString("duration"));
                        taskItem.setStartTime(o.getDate("startTime"));
                        taskItem.setEndTime(o.getDate("endTime"));
                        taskItem.setCatId(o.getString("catId"));
                        taskItem.setPrice(o.getInt("cost"));
                        taskItem.setClientId(o.getString("clientId"));

                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                            taskItem.setImage(bmp);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        tasks.add(taskItem);
                    }
                    ((ArbitorRecyclerAdapter) mAdapter).setTasks(tasks);
                } else {
                    Toast.makeText(ListArbitorActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
