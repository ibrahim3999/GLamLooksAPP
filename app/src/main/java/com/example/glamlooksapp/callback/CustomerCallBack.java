package com.example.glamlooksapp.callback;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;

public interface CustomerCallBack {
    void onAddICustomerComplete(Task<Void> task);
    void onFetchCustomerComplete(ArrayList<User> customers);

//    void onFetchCustomerDatesComplete(ArrayList<Datetime> dateTimes);

}
