package com.example.glamlooksapp.fragments.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.glamlooksapp.Adapter.managerAppointmentListAdapter;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.CustomerManager;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.google.android.gms.tasks.Task;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AppointmentsListMFragment extends Fragment {
    private RecyclerView recyclerViewCustomers;
    private managerAppointmentListAdapter managerAppointmentListAdapter;
    private ArrayList<Customer> appointmentsList;
    Database database;
    Manager currentManager = null;
    AppCompatActivity activity;

    public AppointmentsListMFragment(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;
    }

    public AppointmentsListMFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_times, container, false);
        recyclerViewCustomers = view.findViewById(R.id.recyclerViewCustomers);
        database = new Database();
        appointmentsList = new ArrayList<>();
        //initViews();
        intiVars();
        initRecyclerView();
        Button exportToExcelButton = view.findViewById(R.id.exportToExcelButton);
        exportToExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToExcelAndSendEmail();
            }
        });
        return view;
    }

    private void intiVars() {
        database.setUserCallBack(new UserCallBack() {
            @Override
            public void onManagerFetchDataComplete(Manager manager) throws NoSuchAlgorithmException {
                currentManager = new Manager(manager);
                if (currentManager != null) {
                    Log.d("Manager", "onManagerFetchDataComplete: " + currentManager.getFirstname());
                    //Toast.makeText(activity, currentManager.getService().getServiceName(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Manager", "onManagerFetchDataComplete: " + "NO");
                    //Toast.makeText(activity, "NO", Toast.LENGTH_SHORT).show();
                }
                database.fetchUserDatesByService(currentManager.getService().getServiceName());
            }
            @Override
            public void onCustomerFetchDataComplete(Customer customer) {}
            @Override
            public void onUpdateComplete(Task<Void> task) {}
            @Override
            public void onDeleteComplete(Task<Void> task) {}
        });
        database.setCustomerCallBack(new CustomerCallBack() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCompleteFetchUserDates(ArrayList<Datetime> datetimes) {
                managerAppointmentListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onAddICustomerComplete(Task<Void> task) {}
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFetchCustomerComplete(ArrayList<Customer> customers) {
                if (!customers.isEmpty()) {
                    appointmentsList.clear();
                    appointmentsList.addAll(customers);
                    if (appointmentsList.isEmpty()) {
                        Toast.makeText(activity, "There are no new dates!", Toast.LENGTH_SHORT).show();
                    }
                    managerAppointmentListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, "Customers list is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void exportToExcelAndSendEmail() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Appointments");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Customer FName");
        headerRow.createCell(1).setCellValue("Customer LName");
        headerRow.createCell(2).setCellValue("Customer Phone");
        headerRow.createCell(3).setCellValue("ServiceName");
        headerRow.createCell(4).setCellValue("Date");
        headerRow.createCell(5).setCellValue("Time");
        headerRow.createCell(6).setCellValue("Employee FName");
        headerRow.createCell(7).setCellValue("Employee LName");
        headerRow.createCell(8).setCellValue("Price");
        int rowNum = 1;
        for (Customer customer : appointmentsList) {
            Datetime datetime = customer.getDateTime();

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(customer.getFirstname());
            row.createCell(1).setCellValue(customer.getLastname());
            row.createCell(2).setCellValue(customer.getPhoneNumber());
            row.createCell(3).setCellValue(datetime.getServiceName());
            row.createCell(4).setCellValue(datetime.getFormattedDate());
            row.createCell(5).setCellValue(datetime.getFormattedTime());
            row.createCell(6).setCellValue(currentManager.getFirstname());
            row.createCell(7).setCellValue(currentManager.getLastname());
            row.createCell(8).setCellValue(currentManager.getService().getPrice());

        }
        try {
            File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Appointments.xlsx");
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            Toast.makeText(activity, "Excel file exported to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            sendEmailWithAttachment(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailWithAttachment(File file) {
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{currentManager.getEmail()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Appointments Excel File");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

//    private void initViews() {
//        CustomerManager customerManager = CustomerManager.getInstance();
//        managerAppointmentListAdapter = new managerAppointmentListAdapter(getContext(), customerManager.getCustomerList());
//        recyclerViewCustomers.setAdapter(managerAppointmentListAdapter);
//    }

    private void initRecyclerView() {
        appointmentsList = new ArrayList<>();
        managerAppointmentListAdapter = new managerAppointmentListAdapter(getContext(), appointmentsList);
        recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCustomers.setAdapter(managerAppointmentListAdapter);
        database.fetchManagerData(database.getCurrentUser().getUid());
    }
}
