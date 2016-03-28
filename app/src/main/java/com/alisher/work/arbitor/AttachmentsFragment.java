package com.alisher.work.arbitor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.adapters.AttachAdapter;
import com.alisher.work.adapters.RecyclerItemClickListener;
import com.alisher.work.chat.utils.IOUtil;
import com.alisher.work.models.Attachment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yesmakhan on 25.03.2016.
 */
public class AttachmentsFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    AttachAdapter mAdapter;
    private ArrayList<Attachment> attachments;
    private Button addAttachBtn;
    private ArbitorActivity mainArbitorActivity = (ArbitorActivity) getActivity();

    public AttachmentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attach_fragment, container, false);

        attachments = new ArrayList<>();
        ArbitorActivity list = (ArbitorActivity)getActivity();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.attachFr_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new AttachAdapter(getActivity(), attachments);
        initializeData(list.getMyData().getId());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Attachment catItem = attachments.get(position);
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(catItem.getUrl()));
                startActivity(browseIntent);
            }
        }));
        return v;
    }

    public void initializeData(String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Result");
        query.whereEqualTo("taskId", ParseObject.createWithoutData("Task", task_id));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                attachments.clear();
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        final Attachment aItem = new Attachment();
                        ParseObject o = list.get(i);
                        aItem.setId(o.getObjectId());
                        ParseFile file = (ParseFile) o.get("attach");
                        aItem.setName(file.getName());
                        aItem.setUrl(file.getUrl());
                        aItem.setCreatedAt(o.getCreatedAt());
                        attachments.add(aItem);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
