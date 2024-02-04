package com.example.glamlooksapp.fragments.user;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.collection.CircularArray;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.utils.Datetime;

import java.util.Calendar;
import java.util.Locale;

public class CustomTimePickerDialog extends TimePickerDialog {

    private static final int TIME_PICKER_INTERVAL = 30;
    private boolean mIgnoreEvent = false;
    private List<Datetime> notAvailableTimes;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    // Set the default start and end time
    private static final int START_HOUR = 11;
    private static final int START_MINUTE = 0;
    private static final int END_HOUR = 18;
    private static final int END_MINUTE = 0;
    private   int CUR_HOUR;
    private    int CUR_Minute;

    public CustomTimePickerDialog(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView ,List<Datetime> notAvailableTimes) {
        super(context, themeResId, listener, hourOfDay, minute, is24HourView);
        this.notAvailableTimes=notAvailableTimes;
        this.CUR_HOUR =hourOfDay;
        this.CUR_Minute = minute;

        updateTimeInterval(hourOfDay, minute);
        enforceInitialTimeRange(hourOfDay, minute);

    }

    private void updateTimeInterval(int hourOfDay, int minute) {
        // Set custom time intervals
        int roundedMinute = (minute / TIME_PICKER_INTERVAL) * TIME_PICKER_INTERVAL;
        mIgnoreEvent = true;
        updateTime(hourOfDay, roundedMinute);
        mIgnoreEvent = false;
    }
    private void enforceInitialTimeRange(int hourOfDay, int minute) {
        if (!isTimeBetween(hourOfDay, minute, START_HOUR, START_MINUTE, END_HOUR, END_MINUTE)) {
            mIgnoreEvent = true;
            updateTime(START_HOUR, START_MINUTE);
            mIgnoreEvent = false;
        }
    }
 
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (!mIgnoreEvent) {
            updateTimeInterval(hourOfDay, minute);
            enforceInitialTimeRange(hourOfDay, minute);
            // remove not aviable time
            // removeUnavailableTimes();
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
    }

    // Helper function to check if a given time is between two other times
    private boolean isTimeBetween(int hour, int minute, int startHour, int startMinute, int endHour, int endMinute) {
        if (hour < startHour || (hour == startHour && minute < startMinute)) {
            return false;
        }
        if (hour > endHour || (hour == endHour && minute > endMinute)) {
            return false;
        }
        return true;
    }

    private boolean isUnavailableTime(int hour, int minute) {
        for (Datetime notAvailableTime : notAvailableTimes) {
            if (notAvailableTime.getTimestamp().toDate().getHours() == hour && notAvailableTime.getTimestamp().toDate().getMinutes()== minute) {
                return true;
            }
        }
        return false;
    }
    private Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

}
