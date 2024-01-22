package com.example.glamlooksapp.fragments.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.glamlooksapp.R;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    ImageView hair_cut, hair_color, nails, laser, shaving;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_m, container, false);
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

        // Date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        currentDate.set(Calendar.YEAR, year);
                        currentDate.set(Calendar.MONTH, monthOfYear);
                        currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Time picker dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                requireContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                                        currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        currentDate.set(Calendar.MINUTE, minute);

                                        // Show the selected date and time
                                        showToast("Service: " + serviceName + "\nDate: " +
                                                currentDate.get(Calendar.DAY_OF_MONTH) + "/" +
                                                (currentDate.get(Calendar.MONTH) + 1) + "/" +
                                                currentDate.get(Calendar.YEAR) + "\nTime: " +
                                                currentDate.get(Calendar.HOUR_OF_DAY) + ":" +
                                                currentDate.get(Calendar.MINUTE));
                                    }
                                },
                                currentDate.get(Calendar.HOUR_OF_DAY),
                                currentDate.get(Calendar.MINUTE),
                                false
                        );
                        timePickerDialog.show();
                    }
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
