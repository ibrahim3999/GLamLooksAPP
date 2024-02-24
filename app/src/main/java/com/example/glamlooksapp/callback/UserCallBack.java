package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Customer;
import com.google.android.gms.tasks.Task;

import java.security.NoSuchAlgorithmException;

public interface UserCallBack {
    void onManagerFetchDataComplete(Manager manager) throws NoSuchAlgorithmException;
    void onCustomerFetchDataComplete(Customer customer);
    void onUpdateComplete(Task<Void> task);
    void onDeleteComplete(Task<Void> task); // New method for deletion

//    void onManagerDataFetched(Manager manager);


}