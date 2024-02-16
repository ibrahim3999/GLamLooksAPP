package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.Adapter.CustomersAdapter;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;

import java.security.NoSuchAlgorithmException;

public interface UserCallBack {
    void onUserFetchDataComplete(Manager manager) throws NoSuchAlgorithmException;
    void onUserFetchDataComplete(User user);
    void onUpdateComplete(Task<Void> task);
    void onDeleteComplete(Task<Void> task); // New method for deletion

//    void onManagerDataFetched(Manager manager);


}