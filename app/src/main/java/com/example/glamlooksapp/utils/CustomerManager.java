package com.example.glamlooksapp.utils;


import java.util.ArrayList;

public class CustomerManager {
    private static CustomerManager instance;
    private ArrayList<User> userList;

    private CustomerManager() {
        // Initialize productList or perform any setup
        userList = new ArrayList<>();
    }

    public static synchronized CustomerManager getInstance() {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }

    public ArrayList<User> getCustomerList() {
        return userList;
    }

    public void setCustomerList(ArrayList<User> userList) {
        this.userList = userList;
    }

}

