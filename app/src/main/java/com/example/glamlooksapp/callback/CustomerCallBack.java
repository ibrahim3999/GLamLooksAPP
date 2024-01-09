package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.Customer;
import com.google.android.gms.tasks.Task;

public interface CustomerCallBack {
    void onUserFetchDataComplete(Customer customer);
    void onUpdateComplete(Task<Void> task);

}
