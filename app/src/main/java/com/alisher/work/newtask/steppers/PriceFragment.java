package com.alisher.work.newtask.steppers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.alisher.work.R;
import com.alisher.work.newtask.DataHolder;

import ivb.com.materialstepper.stepperFragment;

/**
 * Created by Alisher Kozhabay on 3/6/2016.
 */
public class PriceFragment extends stepperFragment {
    EditText preciText;

    public PriceFragment() {
    }

    @Override
    public boolean onNextButtonHandler() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_price, container, false);
        preciText = (EditText) view.findViewById(R.id.new_price);
        return view;

    }
}
