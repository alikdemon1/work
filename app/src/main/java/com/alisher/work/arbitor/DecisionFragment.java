package com.alisher.work.arbitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Yesmakhan on 25.03.2016.
 */
public class DecisionFragment extends Fragment {
    private EditText clientTxt;
    private EditText perfTxt, commentEdt;
    private Button saveShareBtn;

    public DecisionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.decision_fragment, container, false);
        clientTxt = (EditText) v.findViewById(R.id.client_share);
        perfTxt = (EditText) v.findViewById(R.id.perf_share);
        saveShareBtn = (Button) v.findViewById(R.id.save_share);
        commentEdt = (EditText) v.findViewById(R.id.comment);

        final ArbitorActivity listT = (ArbitorActivity)getActivity();
        saveShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject>clientQ = ParseQuery.getQuery("Achievement");
                clientQ.whereEqualTo("userId", listT.getMyData().getClientId());
                clientQ.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject o : objects) {
                            Log.d("clientId", listT.getMyData().getClientId());
                            Log.d("clinetFrozen", o.getInt("frozenBalance") + "");
                            Log.d("clientBalance", o.getInt("balance") + " taskPrice:" + listT.getMyData().getPrice());
                            int resClientFrozen = o.getInt("frozenBalance") - (listT.getMyData().getPrice() * Integer.valueOf(perfTxt.getText().toString()) / 100);
                            o.put("frozenBalance", resClientFrozen);
                            int resClientBalance = o.getInt("balance") + (listT.getMyData().getPrice() * Integer.valueOf(clientTxt.getText().toString()) / 100);
                            o.put("balance", resClientBalance);
                            Log.d("clientFrBl", resClientBalance + " fr:" + resClientFrozen);
                            o.saveInBackground();
                        }
                    }
                });
                ParseQuery<ParseObject> queryPerfId = ParseQuery.getQuery("Decision");
                queryPerfId.whereEqualTo("taskId", listT.getMyData().getId());
                queryPerfId.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            for (ParseObject o : list) {
                                final String perfId = o.getString("perfId");
                                ParseQuery<ParseObject>perfQ = ParseQuery.getQuery("Achievement");
                                perfQ.whereEqualTo("userId", perfId);
                                perfQ.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        for (ParseObject perfObj : objects) {
                                            Log.d("performerId", perfId);
                                            Log.d("performerFrozen", perfObj.getInt("frozenBalance") + "");
                                            Log.d("performerBalance", perfObj.getInt("balance") + " taskPrice:" + listT.getMyData().getPrice());
                                            int resPerfBalance = perfObj.getInt("balance") + (listT.getMyData().getPrice() * Integer.valueOf(perfTxt.getText().toString()) / 100);
                                            perfObj.put("balance", resPerfBalance);
                                            perfObj.saveInBackground();
                                            Log.d("resPerfBal", resPerfBalance + "");
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                Toast.makeText(getActivity(), "Shared between them", Toast.LENGTH_SHORT).show();
                deleteFinishedTask(listT.getMyData().getId());
                moveToFinishedStatus(listT.getMyData().getId());
                startActivity(new Intent(getContext(), ListArbitorActivity.class));
            }
        });
        return v;
    }

    public void deleteFinishedTask(final String task_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Decision");
        query.whereEqualTo("taskId", task_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (final ParseObject o : list) {
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Task");
                        parseQuery.whereEqualTo("objectId", task_id);
                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : list) {
                                        obj.put("finishPerfId", o.getString("perfId"));
                                        obj.saveInBackground();
                                    }
                                } else {
                                    Log.d("ChatActivity", e.getMessage());
                                }
                            }
                        });
                        o.deleteEventually();
                    }
                } else {

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
}
