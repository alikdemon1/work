package com.alisher.work.adapters;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.activities.AttachActivity;
import com.alisher.work.activities.ClientDescriptionActivity;
import com.alisher.work.activities.ResultActivity;
import com.alisher.work.chat.ChatActivity;
import com.alisher.work.chat.utils.Const;
import com.alisher.work.chat.utils.Utils;
import com.alisher.work.models.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Yesmakhan on 18.03.2016.
 */
public class ExpandableListAdapterForPerf extends BaseExpandableListAdapter{
    private Context _context;
    private List<String> _listDataHeader;
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
        ImageButton result = (ImageButton) convertView.findViewById(R.id.perf_resultBtn);

        if (groupPosition == 0) {
            desc.setEnabled(true);
            chat.setEnabled(false);
            attach.setEnabled(false);
            result.setEnabled(false);
        } else if (groupPosition == 1) {
            desc.setEnabled(true);
            chat.setEnabled(true);
            attach.setEnabled(true);
            result.setEnabled(true);
        } else if (groupPosition == 2) {
            desc.setEnabled(true);
            chat.setEnabled(false);
            attach.setEnabled(false);
            result.setEnabled(false);
        } else if (groupPosition == 3) {
            desc.setEnabled(true);
            chat.setEnabled(false);
            attach.setEnabled(false);
            result.setEnabled(false);
        } else if (groupPosition == 4) {
            desc.setEnabled(true);
            chat.setEnabled(true);
            attach.setEnabled(false);
            result.setEnabled(false);
        } else if (groupPosition == 5){
            desc.setEnabled(true);
            chat.setEnabled(false);
            attach.setEnabled(false);
            result.setEnabled(false);
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
                    i.putExtra("newTaskCost",childText.getPrice());
                    i.putExtra("newTaskDuration", childText.getEndTime().getTime());
                    i.putExtra("newTaskDeadline", childText.getEndTime().toString());

                    i.putExtra("isVisibleCancel", false);

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
                    openChat(childText.getId(), groupPosition, childPosition);
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

        if(result.isEnabled()){
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ResultActivity.class);
                    i.putExtra("isVisible", true);
                    i.putExtra("isAvailable", false);
                    i.putExtra("task_id", childText.getId());
                    _context.startActivity(i);
                }
            });
        }

        txtListChild.setText(childText.getTitle());
        image.setImageBitmap(childText.getImage());
        return convertView;
    }

    private void openChat(final String task_id, final int group_id, final int child_id) {
        final ProgressDialog dia = ProgressDialog.show(_context, null, _context.getString(R.string.alert_loading));
        ParseQuery<ParseObject> decQuery = ParseQuery.getQuery("Task");
        decQuery.whereEqualTo("objectId", task_id);
        decQuery.include("clientId");
        decQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                dia.dismiss();
                if (e == null) {
                    if (list.size() == 0)
                        Toast.makeText(_context, R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                    ParseUser user = (ParseUser) list.get(0).getParseObject("clientId");
                    _context.startActivity(new Intent(_context,
                            ChatActivity.class).putExtra(
                            Const.EXTRA_DATA, user.getUsername()).putExtra("columnName", "perfId").putExtra("task_id", task_id).putExtra("firstName", user.getString("firstName")));
                } else {
                    Utils.showDialog(
                            _context,
                            _context.getString(R.string.err_users) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }

        });
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
