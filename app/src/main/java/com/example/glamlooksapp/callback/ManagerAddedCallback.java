package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.utils.Manager;

import java.util.ArrayList;

public interface ManagerAddedCallback {
    void onManagerFetchDataComplete(ArrayList<Manager> managerArrayList);
}

