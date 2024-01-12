package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.utils.Customer;

// Define a callback interface for user fetch
public interface UserFetchCallback {
    void onUserFetchDataComplete(Customer customer);
}