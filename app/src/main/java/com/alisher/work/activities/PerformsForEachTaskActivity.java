package com.alisher.work.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.adapters.PerformsForEachTaskAdapter;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.models.Perform;

import java.util.ArrayList;

/**
 * Created by Alisher Kozhabay on 3/13/2016.
 */
public class PerformsForEachTaskActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Perform> pts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pt);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.pt_recycler_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new PerformsForEachTaskAdapter(getApplicationContext());
        initializeData();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Dialog dialog = new Dialog(PerformsForEachTaskActivity.this);
                dialog.setContentView(R.layout.select_dialog);
                dialog.setTitle("Perform details");

                TextView name = (TextView) dialog.findViewById(R.id.movie_name);
                TextView starRate = (TextView) dialog.findViewById(R.id.rate);
                Button ok = (Button) dialog.findViewById(R.id.ok_btn);
                Button cancel = (Button) dialog.findViewById(R.id.cancel_btn);

                Perform perform = pts.get(position);
                name.setText("Perfrom name: " + perform.getName());
                starRate.setText("Perform rating: " + perform.getRating());

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       dialog.cancel();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = getIntent();
                        i.putExtra("child", i.getIntExtra("child_position", 0));
                        i.putExtra("group", i.getIntExtra("group_position", 0));
                        setResult(RESULT_OK, i);
                        finish();
                    }
                });
                dialog.show();
            }
        }));
    }

    public void initializeData(){
        pts = new ArrayList<>();
        pts.add(new Perform("Kozhabay Alisher", 4, R.drawable.ava));
        pts.add(new Perform("Adilov Esmakhan", 3.5f, R.drawable.ava));
        ((PerformsForEachTaskAdapter)mAdapter).setPerformsTask(pts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
