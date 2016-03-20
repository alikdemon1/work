package com.alisher.work.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.adapters.ExpandableListAdapterForPerf;
import com.alisher.work.forTest.WelcomeTestActivity;
import com.alisher.work.models.Task;

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

    List<String> listDataHeader;
    HashMap<String, List<Task>> listDataChild;

    List<Task> inAvailibleList;
    List<Task> inWorkList;
    List<Task> inQueueList;
    List<Task> waitForCheckList;
    List<Task> deniedList;
    List<Task> arbitrageList;
    List<Task> finishedList;

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
        Button tempForm =(Button) v.findViewById(R.id.btnPassTest);
        tempForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Click fab button",Toast.LENGTH_SHORT).show();
            }
        });
        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.expLVPerf);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapterForPerf(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),
                        Toast.LENGTH_SHORT)
                        .show();
                if (groupPosition == 0) {
                    Toast.makeText(getContext(), "groupPosition == 0", Toast.LENGTH_SHORT).show();
                } else if (groupPosition == 1) {
                    Toast.makeText(getContext(), "groupPosition == 1", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"passTest",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), WelcomeTestActivity.class));
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();
        inAvailibleList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        inQueueList = new ArrayList<>();
        waitForCheckList = new ArrayList<>();
        deniedList = new ArrayList<>();
        arbitrageList = new ArrayList<>();
        finishedList = new ArrayList<>();

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
        listDataChild.put(listDataHeader.get(6), finishedList);
    }

}
