package com.alisher.work.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.adapters.ClientAdapter;
import com.alisher.work.models.Task;
import com.alisher.work.newtask.CategoryActivity;
import com.alisher.work.adapters.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by Alisher on 3/1/2016.
 */
public class ClientFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<Task> tasks;

    public ClientFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client, container, false);
        addTask(view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.client_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ClientAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Item = " + position, Toast.LENGTH_SHORT).show();
//                Task taskItem = tasks.get(position);
//                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
//                intent.putExtra("name", taskItem.getName());
//                intent.putExtra("image", taskItem.getImage());
//                startActivity(intent);
            }
        }));
        return view;
    }

    private void addTask(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.addTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Task task = new Task();
            task.setTitle(data.getStringExtra("title"));
            task.setTime(data.getStringExtra("time"));
            task.setPrice(data.getIntExtra("price", 1));
            task.setImage(data.getIntExtra("image_category", 1));
            task.setDesc(data.getStringExtra("desc"));
            String name = data.getStringExtra("name_category");
            Log.d("RECEIVED DATA", task.toString() + " | " + name);
            ((ClientAdapter) mAdapter).addItem(task);
        }
    }
}
