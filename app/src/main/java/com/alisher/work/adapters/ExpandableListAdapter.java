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
 * Created by Alisher Kozhabay on 3/9/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Task>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
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

        _context = parent.getContext();
        final Task childText = (Task) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.client_title);

        ImageView image = (ImageView) convertView
                .findViewById(R.id.client_image);

        ImageButton desc = (ImageButton) convertView.findViewById(R.id.client_descBtn);
        ImageButton chat = (ImageButton) convertView.findViewById(R.id.client_chatBtn);
        ImageButton attach = (ImageButton) convertView.findViewById(R.id.client_attachBtn);
        ImageButton result = (ImageButton) convertView.findViewById(R.id.client_resultBtn);

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
            chat.setEnabled(false);
            attach.setEnabled(false);
            result.setEnabled(false);
        } else if (groupPosition == 5){
            desc.setEnabled(true);
            chat.setEnabled(true);
            attach.setEnabled(true);
            result.setEnabled(true);
        } else if (groupPosition == 6){
            desc.setEnabled(true);
            chat.setEnabled(false);
            attach.setEnabled(false);
            result.setEnabled(false);
        }

        if (desc.isEnabled()) {
            desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(_context, ClientDescriptionActivity.class);
                    i.putExtra("newTaskTitle", childText.getTitle() + "");
                    i.putExtra("newTaskDesc", childText.getDesc() + "");
                    i.putExtra("newTaskId", childText.getId() + "");
                    i.putExtra("newTaskImage", childText.getImage());
                    i.putExtra("newTaskCost", childText.getPrice());
                    i.putExtra("newTaskDeadline", childText.getEndTime().toString());

                    i.putExtra("isVisibleCancel", true);

                    i.putExtra("child", childPosition);
                    i.putExtra("group", groupPosition);
                    i.putExtra("isEnabled", true);
                    _context.startActivity(i);
                }
            });
        }

        if (chat.isEnabled()) {
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChat(childText.getId(), groupPosition, childPosition);
                }
            });
        }

        if (attach.isEnabled()) {
            attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AttachActivity.class);
                    i.putExtra("task_id", childText.getId());
                    i.putExtra("group", groupPosition);
                    i.putExtra("isVisible", false);
                    i.putExtra("isAvailable", true);
                    v.getContext().startActivity(i);
                }
            });
        }

        if(result.isEnabled()){
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ResultActivity.class);
                    i.putExtra("task_id", childText.getId());
                    i.putExtra("isAvailable", true);
                    i.putExtra("newTaskTitle", childText.getTitle());
                    i.putExtra("newTaskDesc", childText.getDesc());
                    i.putExtra("newTaskId", childText.getId());
                    i.putExtra("newTaskCost", childText.getPrice());
                    i.putExtra("newTaskDeadline", childText.getEndTime().toString());
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
                            Toast.makeText(_context, R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(_context, ChatActivity.class);
                        i.putExtra(Const.EXTRA_DATA, list.get(0).getUsername());
                        i.putExtra("firstName", list.get(0).getString("firstName"));
                        i.putExtra("task_id", task_id);
                        i.putExtra("group", group_id);
                        i.putExtra("child", child_id);
                        i.putExtra("columnName", "clientId");
                        _context.startActivity(i);
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
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
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
