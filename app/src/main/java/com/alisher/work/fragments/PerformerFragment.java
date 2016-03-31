package com.alisher.work.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.TaskDescriptionActivity;
import com.alisher.work.adapters.ExpandableListAdapter;
import com.alisher.work.adapters.ExpandableListAdapterForPerf;
import com.alisher.work.chat.ChatActivity;
import com.alisher.work.chat.UserListActivity;
import com.alisher.work.chat.utils.Const;
import com.alisher.work.chat.utils.Utils;
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
import java.util.Timer;
import java.util.TimerTask;

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
    private Timer t;
    private boolean paused;
    private List<Task> inCheckList;
    private List<Task> rejectionList;

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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                resfreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapterForPerf(getActivity(), listDataHeader, listDataChild);
        resfreshData();
        repeatRefresh();
        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Task newTask = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
//                Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : "
//                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),
//                        Toast.LENGTH_SHORT)
//                        .show();

                if (groupPosition == 0) { // Available list
                    Intent i = new Intent(getActivity(), TaskDescriptionActivity.class);
                    i.putExtra("newTaskTitle", newTask.getTitle() + "");
                    i.putExtra("newTaskDesc", newTask.getDesc() + "");
                    i.putExtra("newTaskId", newTask.getId() + "");
                    i.putExtra("newTaskImage", newTask.getImage());
                    i.putExtra("newTaskCost", String.valueOf(newTask.getPrice()));
                    i.putExtra("newTaskDeadline", newTask.getEndTime().toString());
                    startActivity(i);

                } else if (groupPosition == 1) {  //Work list
                    openChat(newTask.getId());
                } else if (groupPosition == 2){ //Arbitrage

                } else if (groupPosition == 3){ //Finished

                } else if (groupPosition == 4){ //Check

                } else if (groupPosition == 5){ //Rejection

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

    private void repeatRefresh() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (paused) {
                    Log.d("COUNTER", "counter++");
                    resfreshData();
                }
            }
        }, 5000, 100000);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("RESUME", "RESUME");
        paused = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("STOP", "STOP");
        paused = false;
    }

    private void passTest(View v) {
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.passTest);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
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
        inCheckList = new ArrayList<>();
        rejectionList = new ArrayList<>();

        initStatusList();
    }

    private void resfreshData() {
        initAvailibleList();
        initFinishedList();
        initWorkList();
        initArbitrageList();
        initCheckList();
        initRejectionList();
    }

    private void initStatusList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");
        query.whereNotEqualTo("name", "Draft");
        query.orderByAscending("createdAt");
        try {
            List<ParseObject> statusList = query.find();
            for (ParseObject obj : statusList) {
                listDataHeader.add(obj.getString("name"));
            }
            listDataChild.put(listDataHeader.get(0), inAvailibleList);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listDataChild.put(listDataHeader.get(2), arbitrageList);
            listDataChild.put(listDataHeader.get(3), finishedList);
            listDataChild.put(listDataHeader.get(4), inCheckList);
            listDataChild.put(listDataHeader.get(5), rejectionList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initCheckList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "o2bnAc0fEy");
        query.whereEqualTo("checkPerfId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("statusId", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                inCheckList.clear();
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
                        task.setPrice(o.getInt("cost"));
                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        task.setImage(bmp);
                        inCheckList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initRejectionList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "1Ts4KSabKd");
        query.whereEqualTo("rejectPerfId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("statusId", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                rejectionList.clear();
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
                        task.setPrice(o.getInt("cost"));
                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        task.setImage(bmp);
                        rejectionList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initFinishedList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "FskciSuqTW");
        query.whereEqualTo("finishPerfId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("statusId", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                finishedList.clear();
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
                        task.setPrice(o.getInt("cost"));
                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        task.setImage(bmp);
                        finishedList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void initArbitrageList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "Y5lhU6qfgB");
        query.whereEqualTo("arbitragePerfId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("statusId", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                arbitrageList.clear();
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
                        task.setPrice(o.getInt("cost"));
                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        task.setImage(bmp);
                        arbitrageList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initWorkList() {
        ParseQuery<ParseObject> queryForId = ParseQuery.getQuery("Decision");
        queryForId.whereEqualTo("perfId", ParseUser.getCurrentUser().getObjectId());
        queryForId.whereEqualTo("clientDec", true);
        try {
            taskIdOnWorkList.clear();
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
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                inWorkList.clear();
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
                        task.setPrice(o.getInt("cost"));
                        ParseFile image = (ParseFile) o.get("img");
                        try {
                            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        task.setImage(bmp);
                        inWorkList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void initAvailibleList() {
        availibleCategories = new ArrayList<>();
        try {
            ParseQuery<ParseObject> qTest = ParseQuery.getQuery("Test");
            qTest.whereEqualTo("perfId", ParseUser.getCurrentUser().getObjectId());
            qTest.whereGreaterThanOrEqualTo("result", 1);
            availibleCategories.clear();
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
            query.whereNotEqualTo("clientId", ParseUser.getCurrentUser());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    inAvailibleList.clear();
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
                            task.setPrice(o.getInt("cost"));
                            ParseFile image = (ParseFile) o.get("img");
                            try {
                                bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            task.setImage(bmp);
                            inAvailibleList.add(task);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openChat(final String task_id) {
        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));
        ParseQuery<ParseObject> decQuery = ParseQuery.getQuery("Task");
        decQuery.whereEqualTo("objectId", task_id);
        decQuery.include("clientId");
        decQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                dia.dismiss();
                if (e == null) {
                    if (list.size() == 0)
                        Toast.makeText(getActivity(), R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                    ParseUser user = (ParseUser) list.get(0).getParseObject("clientId");
                    startActivity(new Intent(getActivity(),
                            ChatActivity.class).putExtra(
                            Const.EXTRA_DATA, user.getUsername()).putExtra("columnName", "perfId").putExtra("task_id", task_id).putExtra("firstName", user.getString("firstName")));
                } else {
                    Utils.showDialog(
                            getActivity(),
                            getString(R.string.err_users) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }

        });
    }
}
