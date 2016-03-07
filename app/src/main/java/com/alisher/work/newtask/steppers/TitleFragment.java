package com.alisher.work.newtask.steppers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alisher.work.R;
import com.alisher.work.newtask.DataHolder;

import ivb.com.materialstepper.stepperFragment;

/**
 * Created by Alisher Kozhabay on 3/6/2016.
 */
public class TitleFragment extends stepperFragment {
    private EditText titleText;
    private EditText descText;

    public TitleFragment() {
    }

    @Override
    public boolean onNextButtonHandler() {
        DataHolder.getInstance().setTitle(titleText.getText().toString());
        DataHolder.getInstance().setDescription(descText.getText().toString());
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_title, container, false);
        titleText = (EditText) view.findViewById(R.id.new_title);
        descText = (EditText) view.findViewById(R.id.new_desc);
        return view;
    }

}
