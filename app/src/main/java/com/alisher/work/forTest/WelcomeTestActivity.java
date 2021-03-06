package com.alisher.work.forTest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Category;
import com.alisher.work.newtask.CategoryAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WelcomeTestActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Category> categories;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = (RecyclerView) findViewById(R.id.test_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new CategoryAdapter(getApplicationContext());
        initializeData();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category catItem = categories.get(position);
                check(catItem);
            }
        }));
    }

    private void check(final Category cat){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Test");
        query.whereEqualTo("perfId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("catId", cat.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.isEmpty()) {
                        Intent i = new Intent(WelcomeTestActivity.this, QuizActivity.class);
                        i.putExtra("catId", cat.getId());
                        startActivity(i);
                    } else {
                        ParseObject obj = objects.get(0);
                        if (obj.getInt("result") == 0) {
                            Date createdDate = getEndDate(obj.getCreatedAt());
                            Date nowDate = new Date();
                            if (nowDate.compareTo(createdDate) > 0){
                                Intent i = new Intent(WelcomeTestActivity.this, QuizActivity.class);
                                i.putExtra("catId", cat.getId());
                                startActivity(i);
                            } else {
                                Toast.makeText(WelcomeTestActivity.this, "Please retry after 2 weeks", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WelcomeTestActivity.this, "Test already passed", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private Date getEndDate(Date date) {
        date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 14);
        date = c.getTime();
        return date;
    }


    public void initializeData(){
        categories = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        final Category catItem = new Category();
                        ParseObject o = list.get(i);
                        catItem.setId(o.getObjectId());
                        catItem.setName(o.getString("name"));
                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                            catItem.setImage(bmp);
                        } catch (com.parse.ParseException e1) {
                            e1.printStackTrace();
                        }
                        categories.add(catItem);
                    }
                    ((CategoryAdapter) mAdapter).setCategories(categories);
                } else {
                    Toast.makeText(WelcomeTestActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ((CategoryAdapter)mAdapter).setCategories(categories);
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
