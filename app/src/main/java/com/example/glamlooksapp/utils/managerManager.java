package com.example.glamlooksapp.utils;


import java.util.ArrayList;

public class managerManager {
    private static managerManager instance;
    private ArrayList<Manager> managerList;

    private ArrayList<Datetime> dateList;
    private managerManager() {
        // Initialize productList or perform any setup
        managerList = new ArrayList<>();
        dateList = new ArrayList<>();

    }

    public static synchronized managerManager getInstance() {
        if (instance == null) {
            instance = new managerManager();
        }
        return instance;
    }

    public ArrayList<Manager> getManagerList() {
        return managerList;
    }

    public ArrayList<Datetime> getDateList() {
        return dateList;
    }

    public void setManagerList(ArrayList<Manager> customersList) {
        this.managerList = customersList;
    }

    public void setDateList(ArrayList<Datetime> dateList) {
        this.dateList = dateList;
    }

}

