package com.example.glamlooksapp.fragments.user;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.glamlooksapp.R;

import java.util.Calendar;


public class HomeFragment extends Fragment {


    private AlertDialog.Builder alertDialog;
    private CalendarView calendarView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }


}