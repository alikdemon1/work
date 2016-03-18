package com.alisher.work.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.ChatActivity;
import com.alisher.work.activities.PerformsForEachTaskActivity;
import com.alisher.work.adapters.ExpandableListAdapter;
import com.alisher.work.models.Category;
import com.alisher.work.models.Task;
import com.alisher.work.newtask.CategoryActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alisher on 3/1/2016.
 */
public class ClientFragment extends Fragment {

    // ExpandableList
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    List<String> listDataHeader;
    HashMap<String, List<Task>> listDataChild;

    List<Task> inSearchList;
    List<Task> inWorkList;
    List<Task> inQueueList;
    List<Task> waitForCheckList;
    List<Task> deniedList;
    List<Task> arbitrageList;
    List<Task> finishedList;

    // RecyclerView
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client, container, false);
        addTask(view);
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),
                        Toast.LENGTH_SHORT)
                        .show();
                if (groupPosition == 0){
                    Intent i = new Intent(getActivity(), PerformsForEachTaskActivity.class);
                    i.putExtra("group_position", childPosition);
                    i.putExtra("child_position", childPosition);
                    startActivityForResult(i, 2);
                } else if (groupPosition == 1){
                    startActivity(new Intent(getActivity(), ChatActivity.class));
                }

                return false;
            }
        });

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
            }
        });

//        mRecyclerView = (RecyclerView) view.findViewById(R.id.client_recycler_view);
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mAdapter = new ClientAdapter(getActivity());
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), "Clicked Item = " + position, Toast.LENGTH_SHORT).show();
////                Task taskItem = tasks.get(position);
////                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
////                intent.putExtra("name", taskItem.getName());
////                intent.putExtra("image", taskItem.getImage());
////                startActivity(intent);
//            }
//        }));
        return view;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();
        inSearchList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        inQueueList = new ArrayList<>();
        waitForCheckList = new ArrayList<>();
        deniedList = new ArrayList<>();
        arbitrageList = new ArrayList<>();
        finishedList = new ArrayList<>();

        listDataHeader.add("In Search");
        listDataHeader.add("In the work");
        listDataHeader.add("In the Queue");
        listDataHeader.add("Waiting for Check");
        listDataHeader.add("Denied");
        listDataHeader.add("Arbitrage");
        listDataHeader.add("Finished");

        listDataChild.put(listDataHeader.get(0), inSearchList);
        listDataChild.put(listDataHeader.get(1), inWorkList);
        listDataChild.put(listDataHeader.get(2), inQueueList);
        listDataChild.put(listDataHeader.get(3), waitForCheckList);
        listDataChild.put(listDataHeader.get(4), deniedList);
        listDataChild.put(listDataHeader.get(5), arbitrageList);
        listDataChild.put(listDataHeader.get(6), finishedList);
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
            inSearchList.add(task);
            listDataChild.put(listDataHeader.get(0), inSearchList); // Header, Child data
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } else if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            int child = data.getIntExtra("child",0);
            int group = data.getIntExtra("group",0);
            Task newTask = listDataChild.get(listDataHeader.get(group)).get(child);
            listDataChild.get(listDataHeader.get(group)).remove(child);
            inWorkList.add(newTask);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            Log.d("NEW_TASK", newTask.toString());
        }
    }
}