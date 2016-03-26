package com.alisher.work.arbitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alisher.work.R;

/**
 * Created by Yesmakhan on 25.03.2016.
 */
public class DecisionFragment extends Fragment {
    private EditText clientTxt;
    private EditText perfTxt;
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

        return v;
    }
}
