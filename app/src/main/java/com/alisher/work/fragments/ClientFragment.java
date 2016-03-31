package com.alisher.work.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.PerformsForEachTaskActivity;
import com.alisher.work.adapters.ExpandableListAdapter;
import com.alisher.work.chat.ChatActivity;
import com.alisher.work.chat.utils.Const;
import com.alisher.work.chat.utils.Utils;
import com.alisher.work.models.Task;
import com.alisher.work.newtask.CategoryActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    List<Task> arbitrageList;
    List<Task> finishedList;
    List<Task> inCheckList;
    List<Task> rejectionList;
    private Bitmap bmp;

    SwipeRefreshLayout swipeRefreshLayout;
    private List<Task> draftList;
    private Timer t;
    private boolean paused;

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
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresherClient);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        prepareListData();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        refreshData();
        repeatRefresh();
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                Task newTask = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                //Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : " + newTask.toString(), Toast.LENGTH_SHORT).show();
                if (groupPosition == 0) {
                    Intent i = new Intent(getActivity(), PerformsForEachTaskActivity.class);
                    i.putExtra("group_position", groupPosition);
                    i.putExtra("taskId", newTask.getId());
                    i.putExtra("child_position", childPosition);
                    i.putExtra("taskPriceForBalance",newTask.getPrice());
                    startActivityForResult(i, 2);
                } else if (groupPosition == 1) {
                    openChatActivity(newTask.getId(), groupPosition, childPosition);
                } else if (groupPosition == 4) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Update task");
                    alertDialog.setMessage("Resume or Give time?");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getBackFromDraftToSearch();
                        }
                    });
                    alertDialog.setNegativeButton("Give time", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getBackFromDraftToWork();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                } else if (groupPosition == 6){
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Update task");
                    alertDialog.setMessage("Resume?");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getBackFromRejectToSearch();
                        }
                    });
                    alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
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

        return view;
    }

    private void refreshData() {
        checkDateExpiration();
        initSearchList();
        initWorkList();
        initCheckList();
        initRejectionList();
        initFinishedList();
        initDraftList();
        initArbitrageList();
    }

    private void repeatRefresh() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (paused) {
                    Log.d("COUNTER", "counter++");
                    refreshData();
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

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();
        inSearchList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        arbitrageList = new ArrayList<>();
        finishedList = new ArrayList<>();
        draftList = new ArrayList<>();
        inCheckList = new ArrayList<>();
        rejectionList = new ArrayList<>();

        initStatusList();
    }

    private void checkDateExpiration() {
        ArrayList<ParseObject> statusList = new ArrayList<>();
        statusList.add(ParseObject.createWithoutData("Status", "j6hNwQ01bt"));
        ParseQuery<ParseObject> queryParseQuery = ParseQuery.getQuery("Task");
        queryParseQuery.whereEqualTo("clientId", ParseUser.getCurrentUser());
        queryParseQuery.whereEqualTo("attach", null);
        queryParseQuery.whereEqualTo("isStarted", true);
        queryParseQuery.whereContainedIn("statusId", statusList);
        try {
            List<ParseObject> parseObjects = queryParseQuery.find();
            for (ParseObject p : parseObjects) {
                Date now = new Date();
                Date end = p.getDate("endTime");
                if (now.compareTo(end) > 0) {
                    p.put("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
                    p.saveEventually();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getBackFromDraftToSearch() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
        try {
            List<ParseObject> objects = query.find();
            for (ParseObject p : objects) {
                ArrayList<Integer> duration = (ArrayList<Integer>) p.get("duration");
                int day = duration.get(0);
                int hour = duration.get(1);
                int minutes = duration.get(2);
                p.put("endTime", getEndDate(day, hour, minutes));
                p.put("startTime", new Date());
                p.put("statusId", ParseObject.createWithoutData("Status", "vVMYOEUIeY"));
                p.saveEventually();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getBackFromRejectToSearch() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", ParseObject.createWithoutData("Status", "1Ts4KSabKd"));
        try {
            List<ParseObject> objects = query.find();
            for (ParseObject p : objects) {
                ArrayList<Integer> duration = (ArrayList<Integer>) p.get("duration");
                int day = duration.get(0);
                int hour = duration.get(1);
                int minutes = duration.get(2);
                p.put("endTime", getEndDate(day, hour, minutes));
                p.put("startTime", new Date());
                p.put("statusId", ParseObject.createWithoutData("Status", "vVMYOEUIeY"));
                p.saveEventually();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getBackFromDraftToWork() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
        try {
            List<ParseObject> objects = query.find();
            for (ParseObject p : objects) {
                ArrayList<Integer> duration = (ArrayList<Integer>) p.get("duration");
                int day = duration.get(0);
                int hour = duration.get(1);
                int minutes = duration.get(2);
                p.put("endTime", getEndDate(day, hour, minutes));
                p.put("startTime", new Date());
                p.put("statusId", ParseObject.createWithoutData("Status", "j6hNwQ01bt"));
                p.saveEventually();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Date getEndDate(int days, int hours, int min) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        c.add(Calendar.HOUR_OF_DAY, hours);
        c.add(Calendar.MINUTE, min);
        date = c.getTime();
        return date;
    }

    // Init list methods
    private void initStatusList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");
        try {
            List<ParseObject> statusList = query.find();
            for (ParseObject obj : statusList) {
                listDataHeader.add(obj.getString("name"));
            }
            listDataChild.put(listDataHeader.get(0), inSearchList);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listDataChild.put(listDataHeader.get(2), arbitrageList);
            listDataChild.put(listDataHeader.get(3), finishedList);
            listDataChild.put(listDataHeader.get(4), draftList);
            listDataChild.put(listDataHeader.get(5), inCheckList);
            listDataChild.put(listDataHeader.get(6), rejectionList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initSearchList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "vVMYOEUIeY");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                inSearchList.clear();
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
                        inSearchList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initCheckList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "o2bnAc0fEy");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
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
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
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


    private void initDraftList() {
        ParseQuery<ParseObject> queryParseQuery = ParseQuery.getQuery("Task");
        queryParseQuery.whereEqualTo("clientId", ParseUser.getCurrentUser());
        queryParseQuery.whereEqualTo("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
        queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                draftList.clear();
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
                        draftList.add(task);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initWorkList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "j6hNwQ01bt");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", obj);
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
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initFinishedList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "FskciSuqTW");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
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
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void initArbitrageList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "Y5lhU6qfgB");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
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

    // Add new Task
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

    private void openChatActivity(final String task_id, final int group_id, final int child_id) {
        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));
        ParseQuery<ParseObject> decQuery = ParseQuery.getQuery("Decision");
        decQuery.whereEqualTo("taskId", task_id);
        decQuery.whereEqualTo("clientDec", true);
        decQuery.whereEqualTo("perfDec", true);
        List<ParseObject> parseObjects = null;
        try {
            parseObjects = decQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (ParseObject obj : parseObjects) {
            ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
            userParseQuery.whereEqualTo("objectId", obj.getString("perfId"));
            userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    dia.dismiss();
                    if (e == null) {
                        if (list.size() == 0)
                            Toast.makeText(getActivity(), R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra(Const.EXTRA_DATA, list.get(0).getUsername());
                        i.putExtra("task_id", task_id);
                        i.putExtra("group", group_id);
                        i.putExtra("child", child_id);
                        i.putExtra("firstName", list.get(0).getString("firstName"));
                        i.putExtra("columnName", "clientId");
                        startActivity(i);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {

        }
    }
}