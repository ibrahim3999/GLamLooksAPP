package com.example.glamlooksapp.callback;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Datetime;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public interface CustomerCallBack {
    void onAddICustomerComplete(Task<Void> task);
    void onFetchCustomerComplete(ArrayList<Customer> customers);

   void onCompleteFetchUserDates(ArrayList<Datetime> datetimes);

}
