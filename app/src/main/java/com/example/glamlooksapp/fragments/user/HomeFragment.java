package com.example.glamlooksapp.fragments.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.DatetimeCallback;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.example.glamlooksapp.utils.Datetime;
import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.RemoteMessage;
public class HomeFragment extends Fragment{

    ImageView hair_cut, hair_color, nails, laser, shaving;
    TextView appointmentsText;
    private Database database;
    private boolean isTimeSelected = false; // Variable to track whether time is selected
    private List<String> selectedTimeSlots = new ArrayList<>(); // List to store selected time slots
    List<String> AvailableTimerSlots = new ArrayList<>();

    public HomeFragment() {// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_m, container, false);
        database = new Database();
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        appointmentsText = view.findViewById(R.id.upcomingAppointments);
        hair_cut = view.findViewById(R.id.haircut);
        hair_color = view.findViewById(R.id.haircolor);
        laser = view.findViewById(R.id.laser);
        shaving = view.findViewById(R.id.shaving);
        nails = view.findViewById(R.id.nails);
    }

    private void initVars() {

        database.setCustomerCallBack(new CustomerCallBack() {
            @Override
            public void onAddICustomerComplete(Task<Void> task) {
                showToast("TimesUpdated!");
                User currentUser = new User();

                currentUser.setKey(database.getCurrentUser().getUid());
                Log.d("AppointmentInfo", "Appointments:");
                //ArrayList<Datetime> haircut_appointments = database.fetchHaircutsAppointments(currentUser.getKey());

                database.fetchHaircutsAppointments(currentUser.getKey())
                        .addOnCompleteListener(new OnCompleteListener<ArrayList<Datetime>>() {
                            @Override
                            public void onComplete(@NonNull Task<ArrayList<Datetime>> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Datetime> haircut_appointments = task.getResult();
                                    for (Datetime appointment : haircut_appointments) {
                                        appointmentsText.append(" " + appointment.getServiceName() + ": " + appointment.getFormattedDate() + " " + appointment.getFormattedTime() + "\n");
                                    }
                                    // Handle the appointmentsList here
                                } else {
                                    // Handle the error
                                    Exception exception = task.getException();
                                    Log.e("FetchAppointments", "Error: " + exception.getMessage());
                                }
                            }
                        });

                database.fetchNailsAppointments(currentUser.getKey())
                        .addOnCompleteListener(new OnCompleteListener<ArrayList<Datetime>>() {
                            @Override
                            public void onComplete(@NonNull Task<ArrayList<Datetime>> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Datetime> nails_appointments = task.getResult();
                                    for (Datetime appointment : nails_appointments) {
                                        appointmentsText.append(" " + appointment.getServiceName() + ": " + appointment.getFormattedDate() + " " + appointment.getFormattedTime() + "\n");
                                    }
                                    // Handle the appointmentsList here
                                } else {
                                    // Handle the error
                                    Exception exception = task.getException();
                                    Log.e("FetchAppointments", "Error: " + exception.getMessage());
                                }
                            }
                        });

                database.fetchLaserAppointments(currentUser.getKey())
                        .addOnCompleteListener(new OnCompleteListener<ArrayList<Datetime>>() {
                            @Override
                            public void onComplete(@NonNull Task<ArrayList<Datetime>> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Datetime> laser_appointments = task.getResult();
                                    for (Datetime appointment : laser_appointments) {
                                        appointmentsText.append(" " + appointment.getServiceName() + ": " + appointment.getFormattedDate() + " " + appointment.getFormattedTime() + "\n");
                                    }
                                } else {
                                    // Handle the error
                                    Exception exception = task.getException();
                                    Log.e("FetchAppointments", "Error: " + exception.getMessage());
                                }
                            }
                        });

            }

            @Override
            public void onFetchCustomerComplete(ArrayList<User> customers) {
            }
        });


        // Add click listeners to the ImageViews
        hair_cut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker("Haircut");
                // Add any additional action you want
            }

        });

        hair_color.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker("Hair color");
                // Add any additional action you want
            }
        });

        nails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker("Nails");
                // Add any additional action you want
            }
        });

        laser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker("Laser");
                // Add any additional action you want
            }
        });

        shaving.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker("Shaving");
                // Add any additional action you want
            }
        });
    }

    private void showDateTimePicker(final String serviceName) {

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
                                    " than Friday or Saturday.");
                            return;
                        } else {

                            // Time picker dialog
                            TimePickerDialog timePickerDialog = new TimePickerDialog(
                                    requireContext(),
                                    R.style.CustomTimePicker,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                                            currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            currentDate.set(Calendar.MINUTE, minute);

                                            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d",
                                                    currentDate.get(Calendar.HOUR_OF_DAY),
                                                    currentDate.get(Calendar.MINUTE));

                                                // Add queue -> database
                                                addQueueToDB(serviceName, currentDate.getTimeInMillis());
                                            }
                                        
                                    },
                                    currentDate.get(Calendar.HOUR_OF_DAY),
                                    currentDate.get(Calendar.MINUTE),
                                    false
                            );

                            // Set the custom time picker intervals (30 minutes)
                            timePickerDialog.updateTime(11, 0);
                            timePickerDialog.updateTime(17, 30);

                            // Show the time picker dialog
                            timePickerDialog.show();
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


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addQueueToDB(String serviceName, long timestamp) {
        Timestamp timestampDB = new Timestamp(timestamp / 1000, 0);

        Datetime datetime = new Datetime();
        datetime.setServiceName(serviceName);
        datetime.setTimestamp(timestampDB);
        datetime.setKey(database.getCurrentUser().getUid());

        User currentUser = new User();

        currentUser.setKey(database.getCurrentUser().getUid());
        currentUser.setDateTime(datetime);

        // Log the serviceName
        Log.d("AddQueueToDB", "Service Name222: " + serviceName);

        // Save to the database
        database.saveUserTimes(datetime, currentUser, serviceName);
    }


}
