package com.alisher.work.arbitor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Sergey Kompaniyets on 25.03.2016.
 */
public class AttachmentsFragment extends Fragment {

    private TextView nameAttach;
    private TextView commentAttach;
    private String urlFile;

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
        ArbitorActivity list = (ArbitorActivity) getActivity();
        nameAttach = (TextView) v.findViewById(R.id.name_attachAr);
        commentAttach = (TextView) v.findViewById(R.id.text_attachAr);
        getFileForTask(list.getMyData().getId());

        nameAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlFile != null) {
                    Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFile));
                    startActivity(browseIntent);
                } else {
                    Toast.makeText(getActivity(), "Nothing to download", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    public void getFileForTask(String taskId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("objectId", taskId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject p : objects) {
                        ParseFile file = (ParseFile) p.get("attach");
                        String text = p.getString("textForResult");
                        if (text == null) {

                        } else {
                            commentAttach.setText(text + " ");
                        }
                        if (file == null) {
                        } else {
                            String result = file.getName().substring(file.getName().lastIndexOf('-') + 1).trim();
                            urlFile = file.getUrl();
                            nameAttach.setText(result);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
