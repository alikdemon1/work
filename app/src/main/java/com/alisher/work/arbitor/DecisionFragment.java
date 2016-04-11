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
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Kompaniyets on 25.03.2016.
 */
public class DecisionFragment extends Fragment {
    private EditText clientTxt;
    private EditText perfTxt;
    private EditText comment;
    private Button saveShareBtn;
    private String sendClient;
    private String sendPerf;
    private ArrayList<String> reciverList;

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
        comment = (EditText) v.findViewById(R.id.comment);
        saveShareBtn = (Button) v.findViewById(R.id.save_share);
        final ArbitorActivity listT = (ArbitorActivity) getActivity();

        saveShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLIENT _ID", listT.getMyData().getClientId());
                changeBalanceClient(listT);
                changeBalancePerf(listT);
                Toast.makeText(getActivity(), "Shared between them", Toast.LENGTH_SHORT).show();
                moveToFinishedStatus(listT.getMyData().getId());
                Log.d("ID_NOBODY", listT.getMyData().getId());
                sendNotification(listT.getMyData().getId(), listT.getMyData().getClientId());
                deleteFinishedTask(listT.getMyData().getId());
                startActivity(new Intent(getContext(), ListArbitorActivity.class));
            }
        });
        return v;
    }

    private void sendNotification(String idE, final String clientId) {
        reciverList = new ArrayList<>();
        ParseQuery<ParseObject> queryPerfId = ParseQuery.getQuery("Decision");
        queryPerfId.whereEqualTo("taskId", idE);
        queryPerfId.findInBackground(new FindCallback<ParseObject>() {
                                         @Override
                                         public void done(List<ParseObject> objects, ParseException e) {
                                             if (e == null) {
                                                 for (ParseObject o : objects) {
                                                     String perfid = o.getString("perfId");
                                                     final ArrayList<String> strings = new ArrayList<String>();
                                                     reciverList.add(perfid);
                                                     reciverList.add(clientId);
                                                     ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                                                     userParseQuery.whereContainedIn("objectId", reciverList);
                                                     userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                                                         @Override
                                                         public void done(List<ParseUser> objects, ParseException e) {
                                                             if (e == null) {
                                                                 for (ParseUser user : objects) {
                                                                     strings.add(user.getUsername());
                                                                 }
                                                                 Log.d("STRINGS", String.valueOf(strings));
                                                                 ParseQuery pushQuery = ParseInstallation.getQuery();
                                                                 pushQuery.whereContainedIn("email", strings);
                                                                 JSONObject data = null;
                                                                 JSONObject main = null;
                                                                 try {
                                                                     data = new JSONObject();
                                                                     main = new JSONObject();
                                                                     data.put("message", comment.getText().toString());
                                                                     data.put("title", "Arbiter delivered it's verdict");
                                                                     main.put("data", data);
                                                                     main.put("is_background", false);
                                                                     main.put("isChat", true);
                                                                     main.put("isNew", false);
                                                                 } catch (JSONException ex) {
                                                                     ex.printStackTrace();
                                                                 }
                                                                 Log.d("JSON", main.toString());
                                                                 ParsePush push = new ParsePush();
                                                                 push.setQuery(pushQuery);
                                                                 push.setData(main);
                                                                 push.sendInBackground();
                                                             } else {
                                                                 e.printStackTrace();
                                                             }
                                                         }
                                                     });
                                                 }
                                             } else

                                             {
                                                 e.printStackTrace();
                                             }
                                         }
                                     }

        );
    }

    private void changeBalanceClient(final ArbitorActivity listT) {
        ParseQuery<ParseObject> clientQuery = ParseQuery.getQuery("Achievement");
        clientQuery.whereEqualTo("userId", listT.getMyData().getClientId());
        clientQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (ParseObject o : list) {
                    Log.d("clientId", listT.getMyData().getClientId());
                    Log.d("clinetFrozen", o.getInt("frozenBalance") + "");
                    Log.d("clientBalance", o.getInt("balance") + " taskPrice:" + listT.getMyData().getPrice());

                    int resClientFrozen = o.getInt("frozenBalance") - (listT.getMyData().getPrice());
                    int resClientBalance = o.getInt("balance") + (listT.getMyData().getPrice() * Integer.valueOf(clientTxt.getText().toString()) / 100);
                    Log.d("clientFrBl", resClientBalance + " fr:" + resClientFrozen);

                    o.put("frozenBalance", resClientFrozen);
                    o.put("balance", resClientBalance);
                    o.saveInBackground();
                }
            }
        });
    }

    private void changeBalancePerf(final ArbitorActivity listT) {
        ParseQuery<ParseObject> queryPerfId = ParseQuery.getQuery("Decision");
        queryPerfId.whereEqualTo("taskId", listT.getMyData().getId());
        queryPerfId.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        final String perfId = o.getString("perfId");
                        ParseQuery<ParseObject> perfQuery = ParseQuery.getQuery("Achievement");
                        perfQuery.whereEqualTo("userId", perfId);
                        perfQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                for (ParseObject perfObj : list) {
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
