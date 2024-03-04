package com.example.glamlooksapp.fragments.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.DatetimeCallback;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import org.apache.poi.ss.formula.functions.DMin;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DynamicHourButtonsFragment extends Fragment implements UserCallBack, CustomerCallBack {
    private static final String TAG = "DynamicButtonsFragment";
    private static final int START_TIME = 11;
    private static final int END_TIME =18;
    private ArrayList<Datetime> notAvailableDates = new ArrayList<>();
    private ArrayList<Datetime> availableDates = new ArrayList<>();
    ;
    private ArrayList<Datetime> curr_dateTimes_manager = new ArrayList<>(); //All appointments scheduled with the current manager
    private Database database;
    private Datetime current_dateTime;
    private long timestamp;
    private User user;
    private View view;
    private ViewGroup buttonContainer;
    private AppCompatActivity activity;
    private  Manager currManager;


    public DynamicHourButtonsFragment(Datetime current_dateTime,Manager manager) {
        this.current_dateTime = current_dateTime;
        activity = new AppCompatActivity();
        currManager= manager;
    }


    private void addTimeSlots(View view) {
        buttonContainer = view.findViewById(R.id.button_container);

        Calendar curr = Calendar.getInstance();
        int currnetHour = curr.get(Calendar.HOUR_OF_DAY);
        int currnetMinute= curr.get(Calendar.MINUTE);
        int duration = Integer.parseInt(currManager.getService().getDuration()); // Parse duration to integer
        int minute=0;
        for (int hour = START_TIME; hour < END_TIME; hour++) {
            for ( minute = 0; minute < 60; minute += duration){
                // Create button text for the current time slot
                String buttonText = String.format("%02d:%02d", hour, minute);
                if (isAvailableTime(hour, minute)) {
                    // Create a button for the available time slot
                    //Log.d(TAG, buttonText + "size :" + notAvailableDates);
                    addButton(buttonContainer, buttonText);
                }
                if(minute +duration > 60){
                    minute =minute -60;
                    if(hour >= END_TIME)break;
                    hour++;
                }
            }
        }
    }

    private boolean isAvailableTime(int hour, int minute) {
        Date currentDate = new Date();
        Calendar curr = Calendar.getInstance();
        curr.setTime(currentDate);
        int currentHour = curr.get(Calendar.HOUR_OF_DAY);
        int currentMinute = curr.get(Calendar.MINUTE);

        // Check if the current date matches the appointment date
        if (curr.getTime().getDate() == current_dateTime.getTimestamp().toDate().getDate()) {
            // Check if the current time slot is in the past
            if (hour < currentHour || (hour == currentHour && minute < currentMinute)) {
                // Time slot is in the past
                return false;
            }
        }

        // Loop through the list of not available dates
        for (Datetime notAvailableDate : notAvailableDates) {
            // Extract the hour and minute from the not available date
            int notAvailableHour = notAvailableDate.getTimestamp().toDate().getHours();
            int notAvailableMinute = notAvailableDate.getTimestamp().toDate().getMinutes();

            // Check if the current time slot conflicts with the not available date
            if (hour == notAvailableHour && minute == notAvailableMinute) {
                // The time slot is not available
                return false;
            }
        }
        // The time slot is available
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = new Database();
        View view = inflater.inflate(R.layout.fragment_dynamic_buttons, container, false);


        database.fetchUserDatesByKeyAndDate(current_dateTime.getManagerId(), current_dateTime.getFormattedDate(), DynamicHourButtonsFragment.this);

        this.view = view;


        return view;
    }

    private void addButton(View view, String buttonText) {
        if (getContext() != null) {


            Button button = new Button(getContext());
            button.setText(buttonText);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(getContext(), ""+current_dateTime.getTimestamp().toDate().getHours(),Toast.LENGTH_SHORT).show();
                    Calendar calendar = Calendar.getInstance();

                    Date date = current_dateTime.getTimestamp().toDate();

                    String times[] = buttonText.trim().split(":");
                    //Toast.makeText(getContext(), ""+times[0]+":"+times[1],Toast.LENGTH_SHORT).show();

                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));

                    // Create a new Date object with the updated values
                    Date newDate = calendar.getTime();

                    // Convert the new Date object back to a Timestamp
                    Timestamp newTimestamp = new Timestamp(newDate);

                    // Update the Datetime object with the new timestamp
                    current_dateTime.setTimestamp(newTimestamp);

                    database.fetchUserData(database.getCurrentUser().getUid(), DynamicHourButtonsFragment.this);
                    checkPastAppointments();
                }
            });
            // You can set onClickListener or any other properties for the button here
            ((ViewGroup) view.findViewById(R.id.button_container)).addView(button);
        } else {
            Log.e(TAG, "buttonContainer is null. Cannot add button.");
        }
    }


    @Override
    public void onManagerFetchDataComplete(Manager manager) throws NoSuchAlgorithmException {

    }

    @Override
    public void onCustomerFetchDataComplete(Customer user) {
        if (user != null) {
            Log.d(TAG, "User Fetch V");
        } else {
            Log.d(TAG, "User data is not available");
        }

    }

    @Override
    public void onUpdateComplete(Task<Void> task) {

    }

    @Override
    public void onDeleteComplete(Task<Void> task) {

    }

    @Override
    public void onAddICustomerComplete(Task<Void> task) {
        if (task != null) {
            Log.d(TAG, "Complete ");
            //Toast.makeText(getContext(), "An appointment was made", Toast.LENGTH_SHORT).show();
            replaceFragment(new HomeFragment()); // Navigate to HomeFragment
        } else {
            Log.d(TAG, "Update Database failed");
            // Optionally, you can navigate to HomeFragment even when there is an error
            replaceFragment(new HomeFragment()); // Navigate to HomeFragment
        }
    }

    @Override
    public void onFetchCustomerComplete(ArrayList<Customer> customers) {

    }


    @Override
    public void onCompleteFetchUserDates(ArrayList<Datetime> datetimes) {
        if (datetimes != null && !datetimes.isEmpty()) {
            this.notAvailableDates = datetimes;
            clearButtons();
            addTimeSlots(view);
        }
        if (datetimes != null && datetimes.isEmpty()) {
            this.notAvailableDates = datetimes;
            clearButtons();
            addTimeSlots(view);
        }

    }

    private void clearButtons() {
        if (buttonContainer != null) {
            if (buttonContainer.getChildCount() == 0) {
                // Hide the button container if there are no buttons
                buttonContainer.setVisibility(View.GONE);
            } else {
                // Clear all buttons
                buttonContainer.removeAllViews();
            }
        } else {
            Log.e(TAG, "buttonContainer is null. Cannot clear buttons.");
        }
    }

    private void checkPastAppointments() {
        database.fetchDatesByManagerId(current_dateTime.getManagerId(), new DatetimeCallback() {
            @Override
            public void onDatetimeFetchComplete(ArrayList<Datetime> datetimes) {

                if (getContext() != null) {
                    boolean hasPastAppointments = false;
                    if (datetimes != null) {
                        for (Datetime datetime : datetimes) {
                            if (datetime.getManagerId().equals(current_dateTime.getManagerId()) && datetime.getKey().equals(current_dateTime.getKey())) {
                                hasPastAppointments = true;
                            }
                        }

                        if (!hasPastAppointments) {
                            database.saveCustomerAppointment(current_dateTime, DynamicHourButtonsFragment.this);
                            Toast.makeText(getContext(), "An appointment was made", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "You cannot make two appointments for the same employer ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the case where datetimes is null
                        Log.e(TAG, "No past appointments data available.");
                    }
                } else {
                    Log.e(TAG, "getContext() returned null");
                }
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerC, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}