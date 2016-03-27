package com.alisher.work.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.activities.AttachActivity;
import com.alisher.work.activities.ClientDescriptionActivity;
import com.alisher.work.models.Task;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Yesmakhan on 18.03.2016.
 */
public class ExpandableListAdapterForPerf extends BaseExpandableListAdapter{
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Task>> _listDataChild;

    public ExpandableListAdapterForPerf(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Task>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Task childText = (Task) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_view, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.perf_title);
        ImageView image = (ImageView) convertView
                .findViewById(R.id.perf_image);

        ImageButton desc = (ImageButton) convertView.findViewById(R.id.perf_descBtn);
        ImageButton chat = (ImageButton) convertView.findViewById(R.id.perf_chatBtn);
        ImageButton attach = (ImageButton) convertView.findViewById(R.id.perf_attachBtn);
        if (groupPosition == 0) {
            desc.setEnabled(true);
            chat.setEnabled(false);
            attach.setEnabled(false);
        } else if (groupPosition == 1) {
            desc.setEnabled(true);
            chat.setEnabled(true);
            attach.setEnabled(true);
        } else if (groupPosition == 4) {
            desc.setEnabled(false);
            chat.setEnabled(false);
            attach.setEnabled(false);
        }


        if (desc.isEnabled()){
            desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ClientDescriptionActivity.class);
                    i.putExtra("newTaskTitle", childText.getTitle() + "");
                    i.putExtra("newTaskDesc", childText.getDesc() + "");
                    i.putExtra("newTaskId", childText.getId() + "");
                    i.putExtra("newTaskImage",childText.getImage());
                    i.putExtra("newTaskCost",String.valueOf(childText.getPrice()));
                    i.putExtra("newTaskDuration", childText.getEndTime().getTime());

                    i.putExtra("child", childPosition);
                    i.putExtra("group", groupPosition);
                    i.putExtra("isEnabled", false);
                    v.getContext().startActivity(i);
                }
            });
        }

        if (chat.isEnabled()){
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        if (attach.isEnabled()){
            attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AttachActivity.class);
                    i.putExtra("task_id", childText.getId());
                    i.putExtra("isVisible", false);
                    v.getContext().startActivity(i);
                }
            });
        }

        txtListChild.setText(childText.getTitle());
        image.setImageBitmap(childText.getImage());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_view, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeaderPerf);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
