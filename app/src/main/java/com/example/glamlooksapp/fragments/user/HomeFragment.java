package com.example.glamlooksapp.fragments.user;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.glamlooksapp.Adapter.ServiceAdapter;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.ManagerAddedCallback;
import com.example.glamlooksapp.callback.OnTextViewClickListener;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Datetime;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements OnTextViewClickListener {
    private Database database;
    private ArrayList<Datetime> appointmentsList = new ArrayList<>();
    private RecyclerView recyclerViewServices;
    private ServiceAdapter serviceAdapter;
    private ArrayList<Manager> managerList = new ArrayList<>();
    private AppCompatActivity activity;
    private  Manager manager;
    public HomeFragment(){}

    public HomeFragment(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_m, container, false);
        database = new Database();
        recyclerViewServices = view.findViewById(R.id.recyclerViewServices);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        managerList = new ArrayList<>();
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceAdapter = new ServiceAdapter(getContext(), managerList,  this);
        recyclerViewServices.setAdapter(serviceAdapter);
        database.fetchManagersData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVars();
    }

    private void initVars() {
        Customer currentCustomer = new Customer();
        currentCustomer.setKey(database.getCurrentUser().getUid());
        database.setCustomerCallBack(new CustomerCallBack() {
            @Override
            public void onCompleteFetchUserDates(ArrayList<Datetime> appointments) {}
            @Override
            public void onAddICustomerComplete(Task<Void> task) {}
            @Override
            public void onFetchCustomerComplete(ArrayList<Customer> customers) {
            }
        });
        database.setOnManagerAddedListener(new ManagerAddedCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onManagerFetchDataComplete(ArrayList<Manager> managerArrayList) {
                if (managerArrayList != null) {
                    Log.d("FireStoreData", "Size of ArrayList Managers is " + managerArrayList.size());
                    managerList.clear();
                    managerList.addAll(managerArrayList);
                    if (managerList.isEmpty()) {
                        Log.d("HomeFragment", "Nothing To Show");
                    }
                    serviceAdapter.notifyDataSetChanged();
                } else {
                    Log.d("HomeFragment", "Managers list is null");
                }
            }
        });
        database.fetchUserDatesByKey(database.getCurrentUser().getUid());
    }


    private void showDateTimePicker(final String serviceName, Datetime datetime) {
        final Calendar currentDate = Calendar.getInstance();
        // Calculate the maximum date (7 days from now)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7);
        // Date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        currentDate.set(Calendar.YEAR, year);
                        currentDate.set(Calendar.MONTH, monthOfYear);
                        currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        // Check if the selected day is Friday or Saturday
                        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                            showToast("Please select another day " +
                                    " than Tuesday or Saturday.");
                        } else {
                           Datetime date =new Datetime();
                            Timestamp timestamp =new Timestamp(currentDate.getTime());
                            date.setTimestamp(timestamp);
                            date.setTimestamp(timestamp);
                            date.setServiceName(serviceName);
                            date.setManagerId(manager.getKey());
                            date.setKey(database.getCurrentUser().getUid());
                            replaceFragment(new DynamicHourButtonsFragment(date));
                        }
                    }
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );
        // Set the minimum and maximum dates for the date picker
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
   public void onTextViewClicked(int position, String managerId) {
        // Retrieve the corresponding Manager object from the managerList
        manager = managerList.get(position);
        // Get the service name from the Manager object
        String serviceName = manager.getService().getServiceName();
        // Create a Datetime object with the details of the manager
        Datetime datetime = new Datetime();
        datetime.setServiceName(serviceName);
        datetime.setTimestamp(new Timestamp(System.currentTimeMillis() / 1000, 0));
        datetime.setKey(database.getCurrentUser().getUid());
        datetime.setUUid("");
        datetime.setManagerId(managerId); // Set the manager ID
        showDateTimePicker(serviceName, datetime);
        Log.d("HomeFragment", "Manager ID: " + managerId);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerC ,fragment);
        fragmentTransaction.commit();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

