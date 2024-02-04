package com.example.glamlooksapp.fragments.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.example.glamlooksapp.callback.DatetimeCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.example.glamlooksapp.utils.Datetime;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
public class HomeFragment extends Fragment{

    ImageView hair_cut, hair_color, nails, laser, shaving;
    private Database database;
    private boolean isTimeSelected = false; // Variable to track whether time is selected
    private List<String> selectedTimeSlots = new ArrayList<>(); // List to store selected time slots
    private  ArrayList<Datetime> datetimes ;

    TextView appointmentsText;

    public HomeFragment() {// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_m, container, false);
        database = new Database();
        datetimes = new ArrayList<Datetime>();
        findViews(view);
        initVars();
        getDatetimesFromDB();
        //ShowDates();
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
        User currentUser = new User();

        currentUser.setKey(database.getCurrentUser().getUid());


        database.setCustomerCallBack(new CustomerCallBack() {

            @Override
            public void onCompleteFetchUserDates(ArrayList<Datetime> datetimes){
                if(datetimes.size()==0){
                    showToast("Null list dates2");
                }
                appointmentsText.setText("");

                for (Datetime appointment : datetimes) {
                    appointmentsText.append(" " + appointment.getServiceName() + ": " + appointment.getFormattedDate() + " " + appointment.getFormattedTime() + "\n");
                }

            }

            @Override
            public void onAddICustomerComplete(Task<Void> task) {
                showToast("TimesUpdated!");
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



        database.fetchUserDatesByKey(currentUser.getKey());

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
                            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(
                                    requireContext(),
                                    R.style.CustomTimePicker,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            currentDate.set(Calendar.MINUTE, minute);

                                            if (isUnavailableTime(hourOfDay, minute)) {
                                                // Handle the case when the chosen time is unavailable
                                                showToast("Selected time is unavailable. Please choose another time.");
                                            } else {
                                                // The chosen time is available, proceed with your logic
                                                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d",
                                                        currentDate.get(Calendar.HOUR_OF_DAY),
                                                        currentDate.get(Calendar.MINUTE));

                                                // Add queue -> database
                                                addQueueToDB(serviceName, currentDate.getTimeInMillis());
                                                // ShowDates();
                                            }
                                        }
                                    },
                                    currentDate.get(Calendar.HOUR_OF_DAY),
                                    currentDate.get(Calendar.MINUTE),
                                    true,
                                    datetimes
                            );


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
        currentUser.setDateTime(datetime);///??New added ,should check!!

        database.saveUserTimes(datetime,currentUser);

        sendMessageToManager(Database.MANAGER_UID,"You Have a New TSchedule");
    }


    private void getDatetimesFromDB() {
        // Assuming 'TSchedule' is the collection name
        database.fetchUserDates(new DatetimeCallback() {
            @Override
            public void onDatetimeFetchComplete(ArrayList<Datetime> Update) {
                datetimes = Update;

            }
        });
    }
    private boolean isUnavailableTime(int hour, int minute) {
        for (Datetime notAvailableTime : datetimes) {
            if (notAvailableTime.getTimestamp().toDate().getHours() == hour && notAvailableTime.getTimestamp().toDate().getMinutes()== minute) {
                return true;
            }
        }
        return false;
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
}