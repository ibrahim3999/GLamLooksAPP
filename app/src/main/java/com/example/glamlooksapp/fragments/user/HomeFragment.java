package com.example.glamlooksapp.fragments.user;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.glamlooksapp.Adapter.ServiceAdapter;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.ManagerAddedCallback;
import com.example.glamlooksapp.callback.OnTextViewClickListener;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.example.glamlooksapp.utils.managerManager;
import com.example.glamlooksapp.callback.DatetimeCallback;
import com.example.glamlooksapp.utils.Datetime;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment implements OnTextViewClickListener {

    private Database database;
    private ArrayList<Datetime> datetimes = new ArrayList<>();
//    private TextView appointmentsText;
    private RecyclerView recyclerViewServices;
    private ServiceAdapter managerAdapter;
    private ArrayList<Manager> managerList = new ArrayList<>();
    private AppCompatActivity activity;
    private  Manager manager ;
    private Manager currentManager = null;
    private LinearLayout appointmentsContainer;


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
        managerAdapter = new ServiceAdapter(getContext(), managerList,  this);
        recyclerViewServices.setAdapter(managerAdapter);
        database.fetchManagersData();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVars();
    }

    private void initVars() {
        User currentUser = new User();
        currentUser.setKey(database.getCurrentUser().getUid());



        database.setCustomerCallBack(new CustomerCallBack() {
            @Override
            public void onCompleteFetchUserDates(ArrayList<Datetime> datetimes) {

            }

            @Override
            public void onAddICustomerComplete(Task<Void> task) {
                showToast("TimesUpdated!");
            }

            @Override
            public void onFetchCustomerComplete(ArrayList<User> customers) {
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
                    managerAdapter.notifyDataSetChanged();
                } else {
                    Log.d("HomeFragment", "Managers list is null");
                }
            }
        });

        database.fetchUserDatesByKey(database.getCurrentUser().getUid());

    }


    private void showDateTimePicker(final String serviceName, Datetime datetime) {

        Datetime new_datetime = new Datetime(datetime);

        // Get the current date and time
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
                            return;
                        } else {
                           Datetime date =new Datetime();

                            Timestamp timestamp =new Timestamp(currentDate.getTime());
                            date.setTimestamp(timestamp);
                            date.setTimestamp(timestamp);
                            date.setServiceName(serviceName);
                            date.setManagerId(manager.getKey());
                            date.setKey(database.getCurrentUser().getUid());
                         // database.fetchUserDatesByKeyAndDate(manager.getKey(),date.getFormattedDate());



                           // DynamicButtonsFragment fragment = new DynamicButtonsFragment(date);
                          //  replaceFragment(fragment);
                            replaceFragment(new DynamicButtonsFragment(date,manager));

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







    private void sendMessageToManager(String managerUserId, String message) {
        // Code to send a message/notification to the manager
        // This could involve using a messaging/notification service like Firebase Cloud Messaging
        // Example:
        Map<String, String> mapMessage = new HashMap<>();
        mapMessage.put("message", message);

        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(managerUserId)
                .setMessageId(UUID.randomUUID().toString())
                .setData(mapMessage)
                .build());
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

        // Show the date and time picker dialog
        showDateTimePicker(serviceName, datetime);
//        sendMessageToManager(manager.getKey(),"You Have a New TSchedule");

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

