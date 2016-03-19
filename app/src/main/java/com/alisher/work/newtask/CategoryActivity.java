package com.alisher.work.newtask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Category;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class CategoryActivity extends AppCompatActivity{
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Category> categories;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.category_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new CategoryAdapter(getApplicationContext());
        initializeData();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category catItem = categories.get(position);
                Intent intent = new Intent(getApplicationContext(), NewTaskActivity.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                catItem.getImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("name", catItem.getName());
                intent.putExtra("id", catItem.getId());
                intent.putExtra("image", byteArray);
                setResult(RESULT_OK, intent);
                startActivityForResult(intent, 2);
            }
        }));
    }

    public void initializeData(){
        categories = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
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
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        categories.add(catItem);
                    }
                    ((CategoryAdapter)mAdapter).setCategories(categories);
                } else {
                    Toast.makeText(CategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ((CategoryAdapter)mAdapter).setCategories(categories);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK){
            Intent i = getIntent();
            i.putExtra("name_category", data.getStringExtra("name_category"));
            i.putExtra("title", data.getStringExtra("title"));
            i.putExtra("time", data.getStringExtra("time"));
            i.putExtra("image_category", data.getByteArrayExtra("image_category"));
            i.putExtra("price", data.getIntExtra("price", 1));
            i.putExtra("desc", data.getStringExtra("desc"));
            setResult(RESULT_OK, i);
            finish();
        }
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
