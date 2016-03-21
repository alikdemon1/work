package com.alisher.work.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.PerformsForEachTaskActivity;
import com.alisher.work.adapters.ExpandableListAdapter;
import com.alisher.work.chat.ChatActivity;
import com.alisher.work.chat.UserListActivity;
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
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Task newTask = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : " + newTask.toString(), Toast.LENGTH_SHORT).show();
                if (groupPosition == 0) {
                    Intent i = new Intent(getActivity(), PerformsForEachTaskActivity.class);
                    i.putExtra("group_position", groupPosition);
                    i.putExtra("taskId", newTask.getId());
                    i.putExtra("child_position", childPosition);
                    startActivityForResult(i, 2);
                } else if (groupPosition == 1) {
                    loadUserList(newTask.getId(), groupPosition, childPosition);
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

    private void loadUserList(final String task_id, final int group_id, final int child_id) {
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
                                startActivityForResult(new Intent(getActivity(),
                                        ChatActivity.class).putExtra(
                                        Const.EXTRA_DATA, list.get(0).getUsername()).putExtra("task_id", task_id).putExtra("group",group_id).putExtra("child", child_id), 3);
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


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();
        inSearchList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        arbitrageList = new ArrayList<>();
        finishedList = new ArrayList<>();

        initStatusList();
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
            listDataChild.put(listDataHeader.get(0), inSearchList);
            listDataChild.put(listDataHeader.get(1), inWorkList);
            listDataChild.put(listDataHeader.get(2), arbitrageList);
            listDataChild.put(listDataHeader.get(3), finishedList);

            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

    public void moveToArbitratorStatus(String id) {
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
            Log.d("NEW_TASK", newTask.toString());
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK){
            int child = data.getIntExtra("child", 0);
            int group = data.getIntExtra("group", 0);
            Task newTask = listDataChild.get(listDataHeader.get(group)).get(child);
            listDataChild.get(listDataHeader.get(group)).remove(child);
            if (data.getStringExtra("flag").equals("arbitrator")){

            } else if (data.getStringExtra("flag").equals("finished")){
                moveToFinishedStatus(newTask.getId());
                finishedList.add(newTask);
                listDataChild.put(listDataHeader.get(3), finishedList);
                listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
            }

        }
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
}