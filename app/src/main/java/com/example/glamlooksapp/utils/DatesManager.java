package com.example.glamlooksapp.utils;


import java.util.ArrayList;

public class DatesManager {
    private static DatesManager instance;
    private ArrayList<Datetime> datesList;
    private ArrayList<Datetime> dateList;

    private DatesManager() {
        // Initialize productList or perform any setup
        datesList = new ArrayList<>();
        dateList = new ArrayList<>();
    }

    public static synchronized DatesManager getInstance() {
        if (instance == null) {
            instance = new DatesManager();
        }
        return instance;
    }

}

