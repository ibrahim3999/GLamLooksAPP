package com.example.glamlooksapp.fragments.user;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomNumberPicker extends NumberPicker {
    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void removeValues(List<String> valuesToRemove) {
        List<String> displayedValuesList = new ArrayList<>(Arrays.asList(getDisplayedValues()));
        for (String valueToRemove : valuesToRemove) {
            displayedValuesList.remove(valueToRemove);
        }
        String[] displayedValuesArray = new String[displayedValuesList.size()];
        displayedValuesList.toArray(displayedValuesArray);
        setDisplayedValues(displayedValuesArray);
        setMinValue(0);
        setMaxValue(displayedValuesArray.length - 1);
    }
}