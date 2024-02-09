package com.example.glamlooksapp.fragments.user;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment implements OnTextViewClickListener {

    private Database database;
    private ArrayList<Datetime> datetimes = new ArrayList<>();
    private TextView appointmentsText;
    private RecyclerView recyclerViewServices;
    private ServiceAdapter managerAdapter;
    private ArrayList<Manager> managerList = new ArrayList<>();
    private AppCompatActivity activity;

    public HomeFragment(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_m, container, false);
        database = new Database();
        recyclerViewServices = view.findViewById(R.id.recyclerViewServices);
        findViews(view);
        initRecyclerView();
//        getDatetimesFromDB();
        return view;
    }

    private void findViews(View view) {
        appointmentsText = view.findViewById(R.id.upcomingAppointments);
    }

    private void initRecyclerView() {
        managerList = new ArrayList<>();
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(getContext()));
        managerAdapter = new ServiceAdapter(getContext(), managerList,  this);
        recyclerViewServices.setAdapter(managerAdapter);
        database.fetchManagersData();
    }

//    private void getDatetimesFromDB() {
//        database.fetchUserDates(new DatetimeCallback() {
//            @Override
//            public void onDatetimeFetchComplete(ArrayList<Datetime> Update) {
//                datetimes = Update;
//            }
//        });
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initRecyclerView();
//        getDatetimesFromDB();
        initVars();
    }

    private void initVars() {
        User currentUser = new User();
        currentUser.setKey(database.getCurrentUser().getUid());

        database.setCustomerCallBack(new CustomerCallBack() {
            @Override
            public void onCompleteFetchUserDates(ArrayList<Datetime> datetimes) {
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

        database.setOnManagerAddedListener(new ManagerAddedCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onManagerFetchDataComplete(ArrayList<Manager> managerArrayList) {
                if (managerArrayList != null) {
                    Log.d("FirestoreData", "Size of ArrayList Managers is " + managerArrayList.size());
                    Toast.makeText(activity, "FetchDoneManagers", Toast.LENGTH_SHORT).show();

                    managerList.clear();
                    managerList.addAll(managerArrayList);
                    if (managerList.isEmpty()) {
                        Toast.makeText(activity, "There are no new managers!", Toast.LENGTH_SHORT).show();
                    }
                    managerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, "Managers list is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        database.fetchUserDatesByKey(database.getCurrentUser().getUid());

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

//                                            if (isUnavailableTime(hourOfDay, minute)) {
//                                                // Handle the case when the chosen time is unavailable
//                                                showToast("Selected time is unavailable. Please choose another time.");
//                                            } else {
                                                // The chosen time is available, proceed with your logic
                                                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d",
                                                        currentDate.get(Calendar.HOUR_OF_DAY),
                                                        currentDate.get(Calendar.MINUTE));

                                                // Add queue -> database
                                                addQueueToDB(serviceName, currentDate.getTimeInMillis());
                                                // ShowDates();
                                            //}
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


    private void addQueueToDB(String serviceName, long timestamp) {

        Timestamp timestampDB = new Timestamp(timestamp / 1000, 0);

        Datetime datetime = new Datetime();
        datetime.setServiceName(serviceName);
        datetime.setTimestamp(timestampDB);
        datetime.setKey(database.getCurrentUser().getUid());
        datetime.setUUid("");
        User currentUser = new User();

        currentUser.setKey(database.getCurrentUser().getUid());
        currentUser.setDateTime(datetime);///??New added ,should check!!

        database.saveUserTimes(datetime,currentUser);

        sendMessageToManager(Database.MANAGER_UID,"You Have a New TSchedule");
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



    @Override
    public void onTextViewClicked(int position) {
        // Retrieve the corresponding Manager object from the managerList
        Manager manager = managerList.get(position);

        // Get the service name from the Manager object
        String serviceName = manager.getService().getServiceName();

        // Show the date and time picker dialog
        showDateTimePicker(serviceName);
    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
