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
    private Bitmap bmp;

    SwipeRefreshLayout swipeRefreshLayout;
    private List<Task> draftList;

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
        refreshDaa(view);
        prepareListData();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                Task newTask = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : " + newTask.toString(), Toast.LENGTH_SHORT).show();
                if (groupPosition == 0) {
                    Intent i = new Intent(getActivity(), PerformsForEachTaskActivity.class);
                    i.putExtra("group_position", groupPosition);
                    i.putExtra("taskId", newTask.getId());
                    i.putExtra("child_position", childPosition);
                    startActivityForResult(i, 2);
                } else if (groupPosition == 1) {
                    openChatActivity(newTask.getId(), groupPosition, childPosition);
                } else if (groupPosition == 4) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Update task");
                    alertDialog.setMessage("Are you sure, that you want to resume the task?");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getBackFromDraft(groupPosition, childPosition);
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

    private void refreshDaa(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresherClient);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                checkDateExpiration();
                inSearchList.clear();
                inWorkList.clear();
                finishedList.clear();
                draftList.clear();
                initSearchList();
                initWorkList();
                initFinishedList();
                initDraftList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();
        inSearchList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        arbitrageList = new ArrayList<>();
        finishedList = new ArrayList<>();
        draftList = new ArrayList<>();

        initStatusList();
    }

    private void checkDateExpiration() {
        ArrayList<ParseObject> statusList = new ArrayList<>();
        statusList.add(ParseObject.createWithoutData("Status", "vVMYOEUIeY"));
        statusList.add(ParseObject.createWithoutData("Status", "j6hNwQ01bt"));
        ParseQuery<ParseObject> queryParseQuery = ParseQuery.getQuery("Task");
        queryParseQuery.whereEqualTo("clientId", ParseUser.getCurrentUser());
        queryParseQuery.whereEqualTo("attach", null);
        queryParseQuery.whereContainedIn("statusId", statusList);
        try {
            List<ParseObject> parseObjects = queryParseQuery.find();
            for (ParseObject p : parseObjects) {
                Date now = new Date();
                Date end = p.getDate("endTime");
                if (now.compareTo(end) > 0) {
                    Log.d("HELlO", now.toString() + ",  " + end.toString());
                    p.put("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
                    p.saveEventually();
                }
                Log.d("HELlO 2", now.toString() + ",  " + end.toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getBackFromDraft(int group, int child) {
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
        Task newTask = listDataChild.get(listDataHeader.get(group)).get(child);
        listDataChild.get(listDataHeader.get(group)).remove(child);
        inSearchList.add(newTask);
        listDataChild.put(listDataHeader.get(0), inSearchList);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
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
    private void initSearchList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "vVMYOEUIeY");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", obj);
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
                inSearchList.add(task);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initStatusList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");
        try {
            List<ParseObject> statusList = query.find();
            for (ParseObject obj : statusList) {
                listDataHeader.add(obj.getString("name"));
            }
            initSearchList();
            initWorkList();
            initFinishedList();
            initDraftList();
            listDataChild.put(listDataHeader.get(0), inSearchList);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listDataChild.put(listDataHeader.get(2), arbitrageList);
            listDataChild.put(listDataHeader.get(3), finishedList);
            listDataChild.put(listDataHeader.get(4), draftList);

            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initDraftList() {
        ParseQuery<ParseObject> queryParseQuery = ParseQuery.getQuery("Task");
        queryParseQuery.whereEqualTo("clientId", ParseUser.getCurrentUser());
        queryParseQuery.whereEqualTo("statusId", ParseObject.createWithoutData("Status", "hPLrQYzPdl"));
        try {
            List<ParseObject> list = queryParseQuery.find();
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
                draftList.add(task);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initWorkList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "j6hNwQ01bt");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", obj);
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

    private void initFinishedList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        ParseObject obj = ParseObject.createWithoutData("Status", "FskciSuqTW");
        query.whereEqualTo("clientId", ParseUser.getCurrentUser());
        query.whereEqualTo("statusId", obj);
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
                finishedList.add(task);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    // Change status methods
    public void moveToWorkStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "j6hNwQ01bt"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO WORK STATUS", e.getMessage());
                }
            }
        });
    }

    public void moveToArbiterStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "Y5lhU6qfgB"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVETARBITRATION STATUS", e.getMessage());
                }
            }
        });
    }

    private void moveToFinishedStatus(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        o.put("statusId", ParseObject.createWithoutData("Status", "FskciSuqTW"));
                        o.saveEventually();
                    }
                } else {
                    Log.d("MOVE TO FINISHED STATUS", e.getMessage());
                }
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
                        startActivityForResult(i, 3);
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
            inSearchList.add(task);
            listDataChild.put(listDataHeader.get(0), inSearchList); // Header, Child data
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            int child = data.getIntExtra("child", 0);
            int group = data.getIntExtra("group", 0);
            Task newTask = listDataChild.get(listDataHeader.get(group)).get(child);
            listDataChild.get(listDataHeader.get(group)).remove(child);
            moveToWorkStatus(newTask.getId());
            inWorkList.add(newTask);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            int child = data.getIntExtra("child", 0);
            int group = data.getIntExtra("group", 0);
            Task newTask = listDataChild.get(listDataHeader.get(group)).get(child);
            listDataChild.get(listDataHeader.get(group)).remove(child);
            moveToFinishedStatus(newTask.getId());
            finishedList.add(newTask);
            listDataChild.put(listDataHeader.get(3), finishedList);
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkDateExpiration();
    }
}