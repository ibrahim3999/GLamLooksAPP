package com.example.glamlooksapp.fragments.user;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.DatetimeCallback;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DynamicButtonsFragment extends Fragment implements UserCallBack, CustomerCallBack {
    private static final String TAG = "DynamicButtonsFragment";
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


    public DynamicButtonsFragment(Datetime current_dateTime) {
        this.current_dateTime = current_dateTime;

        //  initUI();
    }


    private void addTimeSlots(View view) {
        buttonContainer = view.findViewById(R.id.button_container);

        // Create buttons for each available time slot from 11:00 to 18:00 in half-hour intervals
        for (int hour = 11; hour <= 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                // Create button text for the current time slot
                String buttonText = String.format("%02d:%02d", hour, minute);

                if (isAvailableTime(hour, minute)) {
                    // Create a button for the available time slot
                    //Log.d(TAG, buttonText + "size :" + notAvailableDates);
                    addButton(buttonContainer, buttonText);
                }
            }
        }
    }

    private boolean isAvailableTime(int hour, int minute) {
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


        database.fetchUserDatesByKeyAndDate(current_dateTime.getManagerId(), current_dateTime.getFormattedDate(), DynamicButtonsFragment.this);

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

                    database.fetchUserData(database.getCurrentUser().getUid(), DynamicButtonsFragment.this);
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
    public void onUserFetchDataComplete(Manager manager) throws NoSuchAlgorithmException {


    }


    @Override
    public void onUserFetchDataComplete(User user) {
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
    public void onAddICustomerComplete(Task<Void> task) {
        if (task != null) {
            Log.d(TAG, "Complete ");
            Toast.makeText(getContext(), "Your appointment is scheduled at" + current_dateTime.getFormattedTime(), Toast.LENGTH_SHORT).show();
            database.fetchUserDatesByKeyAndDate(current_dateTime.getManagerId(), current_dateTime.getFormattedDate(), DynamicButtonsFragment.this);
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            Log.d(TAG, "Update Database failed");
        }
    }

    @Override
    public void onFetchCustomerComplete(ArrayList<User> customers) {

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
                if (getContext() !=null) {
                    boolean hasPastAppointments = false;
                    if (datetimes != null) {
                        for (Datetime datetime : datetimes) {
                            if (datetime.getManagerId().equals(current_dateTime.getManagerId()) && datetime.getKey().equals(current_dateTime.getKey())) {
                                hasPastAppointments = true;
                            }
                        }
                        if (!hasPastAppointments) {
                            database.saveUserTimes(current_dateTime, user, DynamicButtonsFragment.this);
                        } else {
                                Toast.makeText(getContext(), "You cannot make two appointments for the same employer ", Toast.LENGTH_SHORT).show();
          //                  getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } else {
                        // Handle the case where datetimes is null
                        Log.e(TAG, "No past appointments data available.");
                    }
                }else {
                    Log.e(TAG, "getContext() returned null");
                }
            }
        });
    }
}
