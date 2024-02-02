package com.example.glamlooksapp.fragments.user;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.collection.CircularArray;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import java.util.Calendar;

public class CustomTimePickerDialog extends TimePickerDialog {

    private static final int TIME_PICKER_INTERVAL = 30;
    private boolean mIgnoreEvent = false;

    public CustomTimePickerDialog(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, themeResId, listener, hourOfDay, minute, is24HourView);
        updateTimeInterval(hourOfDay, minute);
    }

    private void updateTimeInterval(int hourOfDay, int minute) {
        // Set custom time intervals
        int roundedMinute = (minute / TIME_PICKER_INTERVAL) * TIME_PICKER_INTERVAL;
        mIgnoreEvent = true;
        updateTime(hourOfDay, roundedMinute);
        mIgnoreEvent = false;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (!mIgnoreEvent) {
            updateTimeInterval(hourOfDay, minute);
        }
    }
}
