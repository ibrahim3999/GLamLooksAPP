package com.example.glamlooksapp.fragments.manager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.glamlooksapp.Adapter.CustomerAdapter;
import com.example.glamlooksapp.Adapter.CustomersAdapter;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.utils.CustomerManager;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class TimesFragment extends Fragment {


    private RecyclerView recyclerViewCustomers;
    private CustomerAdapter customerAdapter;
    private ArrayList<User> customersList;

    Database database;
    Manager currentManager = null;


    AppCompatActivity activity;

    public TimesFragment(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    public TimesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_times, container, false);
        recyclerViewCustomers = view.findViewById(R.id.recyclerViewCustomers);
        database = new Database();
        customersList = new ArrayList<>();

        initViews();
        intiVars();
        initRecyclerView();

        return view;
    }


    private void intiVars() {

        database.setUserCallBack(new UserCallBack() {


            @Override
            public void onUserFetchDataComplete(Manager manager) throws NoSuchAlgorithmException {

                currentManager = new Manager(manager);
                if(currentManager!=null) {
                    Toast.makeText(activity, currentManager.getService().getServiceName(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "NO", Toast.LENGTH_SHORT).show();
                }
                database.fetchUserDatesByService(currentManager.getService().getServiceName());

            }
            @Override
            public void onUserFetchDataComplete(User user) {

            }

            @Override
            public void onUpdateComplete(Task<Void> task) {

            }

            @Override
            public void onDeleteComplete(Task<Void> task) {

            }
        });


        database.setCustomerCallBack(new CustomerCallBack() {
            @Override
            public void onCompleteFetchUserDates(ArrayList<Datetime> datetimes){}

            @Override
            public void onAddICustomerComplete(Task<Void> task) {

            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFetchCustomerComplete(ArrayList<User> customers) {
                if (!customers.isEmpty()) {
                    //Toast.makeText(activity, "FetchDone", Toast.LENGTH_SHORT).show();

                    customersList.clear();
                    customersList.addAll(customers);
                    if (customersList.isEmpty()) {
                        Toast.makeText(activity, "There are no new dates!", Toast.LENGTH_SHORT).show();
                    }
                    customerAdapter.notifyDataSetChanged();
                } else {
                    // Handle the case where 'customers' is null
                    Toast.makeText(activity, "Customers list is null", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

        private void initViews() {
        CustomerManager customerManager = CustomerManager.getInstance();
        customerAdapter = new CustomerAdapter(getContext(), customerManager.getCustomerList());
        recyclerViewCustomers.setAdapter(customerAdapter);
            }

    private void initRecyclerView() {
        customersList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(getContext(), customersList);
        recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCustomers.setAdapter(customerAdapter);
        // Fetch DateTimes from the database and update the list
        database.fetchManagerData(database.getCurrentUser().getUid());

    }
}