package com.alisher.work.newtask.steppers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.alisher.work.R;
import com.alisher.work.newtask.DataHolder;

import ivb.com.materialstepper.stepperFragment;

/**
 * Created by Alisher Kozhabay on 3/6/2016.
 */
public class TimeFragment extends stepperFragment {
    NumberPicker day;
    NumberPicker hours;
    NumberPicker minutes;

    @Override
    public boolean onNextButtonHandler() {
        DataHolder.getInstance().setTime(day.getValue() + " дней" + hours.getValue() + " часов" + minutes.getValue() + " минут");
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_time, container, false);
        initNP(view);
        return view;
    }

    private void initNP(View view){
        day = (NumberPicker) view.findViewById(R.id.time_day);
        hours = (NumberPicker) view.findViewById(R.id.time_hours);
        minutes = (NumberPicker) view.findViewById(R.id.time_minutes);

        day.setMinValue(0);
        day.setMaxValue(365);

        hours.setMinValue(0);
        hours.setMaxValue(23);

        minutes.setMinValue(0);
        minutes.setMaxValue(59);

        day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        hours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        minutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
    }
}
