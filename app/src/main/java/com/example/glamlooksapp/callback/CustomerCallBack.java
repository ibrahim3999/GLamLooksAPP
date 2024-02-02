package com.example.glamlooksapp.callback;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public interface CustomerCallBack {
    void onAddICustomerComplete(Task<Void> task);
    void onFetchCustomerComplete(ArrayList<User> customers);

}
