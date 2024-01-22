package com.example.glamlooksapp.fragments.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.utils.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    ImageView hair_cut, hair_color, nails, laser, shaving;
    private Database database;
    private FirebaseUser firebaseUser;
    private boolean isTimeSelected = false; // Variable to track whether time is selected
    private List<String> selectedTimeSlots = new ArrayList<>(); // List to store selected time slots
    List<String> AvailableTimerSlots = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_m, container, false);
        database = new Database();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        hair_cut = view.findViewById(R.id.haircut);
        hair_color = view.findViewById(R.id.haircolor);
        laser = view.findViewById(R.id.laser);
        shaving = view.findViewById(R.id.shaving);
        nails = view.findViewById(R.id.nails);
    }

    private void initVars() {
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
                            showToast("Please select a day other than Friday or Saturday.");
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


                                                /*
                                                showToast("Service: " + serviceName + "\nDate: " +
                                                        currentDate.get(Calendar.DAY_OF_MONTH) + "/" +
                                                        (currentDate.get(Calendar.MONTH) + 1) + "/" +
                                                        currentDate.get(Calendar.YEAR) + "\nTime: " +
                                                        currentDate.get(Calendar.HOUR_OF_DAY) + ":" +
                                                        currentDate.get(Calendar.MINUTE));
                                            */


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp timestampDB = new Timestamp(timestamp / 1000, 0);

        Map<String, Object> queueData = new HashMap<>();
        queueData.put("uid", firebaseUser.getUid());
        queueData.put("serviceName", serviceName);
        queueData.put("timestamp", timestampDB);

        CollectionReference queuesCollection = db.collection("FuctureQueues");

        queuesCollection.add(queueData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                showToast("Added to firestore: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Error adding to Friebase" + e.getMessage());
            }
        });

    }
}
