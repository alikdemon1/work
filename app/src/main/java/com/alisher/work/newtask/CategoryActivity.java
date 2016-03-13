package com.alisher.work.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.alisher.work.R;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Category;

import java.util.ArrayList;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class CategoryActivity extends AppCompatActivity{
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Category> categories;

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
                intent.putExtra("name", catItem.getName());
                intent.putExtra("image", catItem.getImage());
                setResult(RESULT_OK, intent);
                startActivityForResult(intent, 2);
            }
        }));
    }

    public void initializeData(){
        categories = new ArrayList<>();
        categories.add(new Category("Реклама/соцсети", "(Объявления, отзывы, группы, продвижение)", R.drawable.cat1));
        categories.add(new Category("Простая помощь", "(Скачать, аудио в текст, найти, заполнить сайт)", R.drawable.cat2));
        categories.add(new Category("Дизайн", "(Сайт, визитка, лого, баннер, фото)", R.drawable.cat3));
        categories.add(new Category("Тексты", "(Копирайт, рерайт, коррекция, перевод)", R.drawable.cat4));
        categories.add(new Category("Помощь по сайту", "(Настроить, сверстать, сделать сайт/блог)", R.drawable.cat5));
        categories.add(new Category("Другое", "(Обзвонить, придумать, решить, аудио/видео)", R.drawable.cat6));
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
            i.putExtra("image_category", data.getIntExtra("image_category", 1));
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
