package com.alisher.work.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.alisher.work.R;
import com.alisher.work.adapters.ExpListAdapter;

import java.util.ArrayList;

/**
 * Created by Alisher on 3/1/2016.
 */
public class PerformerFragment extends Fragment {

    ExpandableListView expLV;
    Button bPassTest;
    ArrayList<String> groupNames = new ArrayList<String>();
    ArrayList<String> child1 = new ArrayList<String>();
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
        View v =inflater.inflate(R.layout.fragment_performer, container, false);

        expLV=(ExpandableListView) v.findViewById(R.id.expListView);
        initContentOfExpLV();
        ExpListAdapter adapter = new ExpListAdapter(v.getContext(), groupNames,child1);
        expLV.setAdapter(adapter);

        bPassTest=(Button)v.findViewById(R.id.btnPassTest);
        bPassTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }

    private void initContentOfExpLV() {
        groupNames.add("Availible (0)");
        groupNames.add("In the Work (0)");
        groupNames.add("In the Queue (0)");
        groupNames.add("Waiting for Check (0)");
        groupNames.add("Denied (0)");
        groupNames.add("Arbitrage (0)");
        groupNames.add("Finished (0)");
        child1.add("yet nothing");
    }
}
