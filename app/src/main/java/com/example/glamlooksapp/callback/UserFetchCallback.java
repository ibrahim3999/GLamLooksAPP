package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.utils.User;

// Define a callback interface for user fetch
public interface UserFetchCallback {
    void onUserFetchDataComplete(User user);
}