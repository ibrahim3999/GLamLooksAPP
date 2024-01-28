package com.example.glamlooksapp.utils;


import java.util.ArrayList;

public class CustomerManager {
    private static CustomerManager instance;
    private ArrayList<User> customersList;

    private ArrayList<Datetime> dateList;
    private CustomerManager() {
        // Initialize productList or perform any setup
        customersList = new ArrayList<>();
        dateList = new ArrayList<>();

    }

    public static synchronized CustomerManager getInstance() {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }

    public ArrayList<User> getCustomerList() {
        return customersList;
    }

    public ArrayList<Datetime> getDateList() {
        return dateList;
    }

    public void setCustomerList(ArrayList<User> customersList) {
        this.customersList = customersList;
    }

    public void setDateList(ArrayList<Datetime> dateList) {
        this.dateList = dateList;
    }

}

