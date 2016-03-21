package com.alisher.work.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.TaskDescriptionActivity;
import com.alisher.work.adapters.ExpandableListAdapter;
import com.alisher.work.adapters.ExpandableListAdapterForPerf;
import com.alisher.work.chat.UserListActivity;
import com.alisher.work.forTest.WelcomeTestActivity;
import com.alisher.work.models.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alisher on 3/1/2016.
 */
public class PerformerFragment extends Fragment {

    // ExpandableList
    ExpandableListAdapterForPerf listAdapter;
    ExpandableListView expListView;
    SwipeRefreshLayout swipeRefreshLayout;

    List<String> listDataHeader, availibleCategories;
    HashMap<String, List<Task>> listDataChild;
    List<String> taskIdOnWorkList;
    List<Task> inAvailibleList;
    List<Task> inWorkList;
    List<Task> arbitrageList;
    List<Task> finishedList;
    private Bitmap bmp;

    public PerformerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_performer, container, false);
        passTest(v);
        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.expLVPerf);
        swipeRefreshLayout =(SwipeRefreshLayout) v.findViewById(R.id.swipeRefresher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                inAvailibleList.clear();
                inWorkList.clear();
                initAvailibleList();
                initWorkList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // preparing list data
        prepareListData();

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Task newTask = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),
                        Toast.LENGTH_SHORT)
                        .show();
                if (groupPosition == 0) {
                    Toast.makeText(getContext(), "groupPosition == 0", Toast.LENGTH_SHORT).show();
                    Intent i =new Intent(getActivity(), TaskDescriptionActivity.class);
                    Log.d("asdasdad", newTask.getTitle()+" "+newTask.getDesc());
                    i.putExtra("newTaskTitle", newTask.getTitle()+"");
                    i.putExtra("newTaskDesc",newTask.getDesc()+"");
                    i.putExtra("newTaskId",newTask.getId()+"");
                    startActivity(i);

                } else if (groupPosition == 1) {
                    Toast.makeText(getContext(), "groupPosition == 1", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), UserListActivity.class);
                    intent.putExtra("task_id", newTask.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "else", Toast.LENGTH_SHORT).show();
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
        return v;
    }

    private void passTest(View v) {
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.passTest);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "passTest", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), WelcomeTestActivity.class));
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();
        inAvailibleList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        arbitrageList = new ArrayList<>();
        finishedList = new ArrayList<>();
        taskIdOnWorkList = new ArrayList<String>();
        initStatusList();
        initAvailibleList();
/*
        listDataHeader.add("Availible");
        listDataHeader.add("In the work");
        listDataHeader.add("In the Queue");
        listDataHeader.add("Waiting for Check");
        listDataHeader.add("Denied");
        listDataHeader.add("Arbitrage");
        listDataHeader.add("Finished");

        listDataChild.put(listDataHeader.get(0), inAvailibleList);
        listDataChild.put(listDataHeader.get(1), inWorkList);
        listDataChild.put(listDataHeader.get(2), inQueueList);
        listDataChild.put(listDataHeader.get(3), waitForCheckList);
        listDataChild.put(listDataHeader.get(4), deniedList);
        listDataChild.put(listDataHeader.get(5), arbitrageList);
        listDataChild.put(listDataHeader.get(6), finishedList);*/
    }

    private void initStatusList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");
        try {
            List<ParseObject> statusList = query.find();
            for (ParseObject obj : statusList) {
                listDataHeader.add(obj.getString("name"));
            }
            initWorkList();
            listDataChild.put(listDataHeader.get(0), inAvailibleList);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listDataChild.put(listDataHeader.get(2), arbitrageList);
            listDataChild.put(listDataHeader.get(3), finishedList);

            listAdapter = new ExpandableListAdapterForPerf(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initWorkList() {
        ParseQuery<ParseObject> queryForId = ParseQuery.getQuery("Decision");
        queryForId.whereEqualTo("perfId", ParseUser.getCurrentUser().getObjectId());
        queryForId.whereEqualTo("clientDec", true);
        try {
            List<ParseObject> list = queryForId.find();
            for (ParseObject o : list) {
                taskIdOnWorkList.add(o.getString("taskId"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "j6hNwQ01bt");
        query.whereEqualTo("statusId", obj);
        query.whereContainedIn("objectId", taskIdOnWorkList);
        try {
            List<ParseObject> list = query.find();
            for (ParseObject o : list) {
                Task task = new Task();
                task.setId(o.getObjectId());
                task.setTitle(o.getString("name"));
                task.setDesc(o.getString("description"));
                task.setDuration(o.getString("duration"));
                task.setStartTime(o.getDate("startTime"));
                task.setEndTime(o.getDate("endTime"));
                task.setCatId(o.getString("catId"));
                ParseFile image = (ParseFile) o.get("img");
                bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                task.setImage(bmp);
                inWorkList.add(task);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Task task = new Task();
            task.setTitle(data.getStringExtra("title"));
            task.setDuration(data.getStringExtra("time"));
            task.setPrice(data.getIntExtra("price", 1));
            task.setDesc(data.getStringExtra("desc"));
            byte[] img = data.getByteArrayExtra("image_category");
            Bitmap b = BitmapFactory.decodeByteArray(img, 0, img.length);
            task.setImage(b);
            String name = data.getStringExtra("name_category");
            //Log.d("RECEIVED DATA", task.toString() + " | " + name);
            inAvailibleList.add(task);
            listDataChild.put(listDataHeader.get(0), inAvailibleList); // Header, Child data
            listAdapter = new ExpandableListAdapterForPerf(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            int child = data.getIntExtra("child", 0);
            int group = data.getIntExtra("group", 0);
            Task newTask = listDataChild.get(listDataHeader.get(group)).get(child);
            listDataChild.get(listDataHeader.get(group)).remove(child);
            inWorkList.add(newTask);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listAdapter = new ExpandableListAdapterForPerf(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            Log.d("NEW_TASK", newTask.toString());
        }
    }

    private void initAvailibleList() {
        availibleCategories=new ArrayList<>();
        try {
            ParseQuery<ParseObject> qTest = ParseQuery.getQuery("Test");
            qTest.whereEqualTo("perfId", ParseUser.getCurrentUser().getObjectId());
            qTest.whereGreaterThanOrEqualTo("result", 1);
            List<ParseObject> parseObjects = qTest.find();
            for (ParseObject o : parseObjects) {
                availibleCategories.add(o.getString("catId"));
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
            List<ParseObject> objCat = new ArrayList<>();
            for (int i = 0; i < availibleCategories.size(); i++) {
                objCat.add(ParseObject.createWithoutData("Category", availibleCategories.get(i)));
            }
            ParseObject obj = ParseObject.createWithoutData("Status", "vVMYOEUIeY");
            query.whereEqualTo("statusId", obj);
            query.whereContainedIn("catId", objCat);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        for (ParseObject o : list) {
                            Task task = new Task();
                            task.setId(o.getObjectId());
                            task.setTitle(o.getString("name"));
                            task.setDesc(o.getString("description"));
                            task.setDuration(o.getString("duration"));
                            task.setStartTime(o.getDate("startTime"));
                            task.setEndTime(o.getDate("endTime"));
                            task.setCatId(o.getString("catId"));
                            ParseFile image = (ParseFile) o.get("img");
                            try {
                                bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            task.setImage(bmp);
                            inAvailibleList.add(task);
                        }
                        listDataChild.put(listDataHeader.get(0), inAvailibleList); // Header, Child data
                        listAdapter = new ExpandableListAdapterForPerf(getActivity(), listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
