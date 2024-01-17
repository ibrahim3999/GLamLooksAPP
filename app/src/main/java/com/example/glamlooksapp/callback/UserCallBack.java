package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;

public interface UserCallBack {
    void onUserFetchDataComplete(User customer);
    void onUpdateComplete(Task<Void> task);

}
