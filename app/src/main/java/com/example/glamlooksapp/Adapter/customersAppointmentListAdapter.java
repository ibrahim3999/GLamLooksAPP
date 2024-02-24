package com.example.glamlooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.google.android.gms.tasks.Task;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class customersAppointmentListAdapter extends RecyclerView.Adapter<customersAppointmentListAdapter.CustomersViewHolder> {
    private Context context;
    private ArrayList<Datetime> datetimeArrayList;
    private Database database = new Database();

    public customersAppointmentListAdapter(Context context, ArrayList<Datetime> datetimeArrayList) {
        this.context = context;
        this.datetimeArrayList = datetimeArrayList;
    }

    public void removeItem(int position) {
        datetimeArrayList.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_appoinment_item, parent, false);
        return new CustomersViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CustomersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Datetime datetime = datetimeArrayList.get(position);
        String serviceName = datetime.getServiceName() + " ";
        holder.textViewServName.setText(serviceName);
        holder.textViewTimeDate.setText(datetime.getFormattedDate());
        holder.textViewTime.setText(datetime.getFormattedTime());
        holder.removeButton.setOnClickListener(v -> {
            String datetimeUid = datetime.getUUid();
            database.deleteUserTime(datetimeUid, new UserCallBack() {
                @Override
                public void onManagerFetchDataComplete(Manager manager) throws NoSuchAlgorithmException {
                    Log.d("onManagerFetchDataComplete","onManagerFetchDataComplete is called");
                }
                @Override
                public void onCustomerFetchDataComplete(Customer customer) {
                    Log.d("onCustomerFetchDataComplete","onCustomerFetchDataComplete is called");
                }
                @Override
                public void onUpdateComplete(Task<Void> task) {
                    Log.d("onUpdateComplete","onUpdateComplete is called");
                }
                @Override
                public void onDeleteComplete(Task<Void> task) {
                    if(task != null){
                        Log.d("removeAppointment","Deleting an appointment queue");
                        removeItem(position); // Remove item from RecyclerView
                    } else {
                        Log.e("removeAppointment","Failed Deleted");
                    }
                }
            });
        });

        // Fetch manager's data by ID
        String managerId = datetime.getManagerId();
        Log.d("ManagerId", datetime.getManagerId());

        database.fetchManagerData(managerId, new UserCallBack() {
            @Override
            public void onManagerFetchDataComplete(Manager manager) {
                if (manager != null) {
                    // Set manager's data in the ViewHolder
                    String manager_name = manager.getFirstname() + " " + manager.getLastname();
                    holder.texManagerName.setText(manager_name);
                    holder.texDuration.setText(manager.getService().getDuration());
                } else {
                    Log.d("UserCallBack", "onUserFetchDataComplete is called400");
                }
            }

            @Override
            public void onCustomerFetchDataComplete(Customer customer) {
                Log.d("UserCallBack", "onUserFetchDataComplete is called");
            }

            @Override
            public void onUpdateComplete(Task<Void> task) {
                Log.d("UserCallBack", "onUpdateComplete is called");
            }

            @Override
            public void onDeleteComplete(Task<Void> task) {
                Log.d("UserCallBack", "onDeleteComplete is called");
            }
        });
    }

    @Override
    public int getItemCount() {
        return datetimeArrayList.size();
    }

    public static class CustomersViewHolder extends RecyclerView.ViewHolder {
        TextView textViewServName, textViewTimeDate, textViewTime, texManagerName, texDuration;
        ImageButton removeButton;
        public CustomersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServName = itemView.findViewById(R.id.textViewServName);
            textViewTimeDate = itemView.findViewById(R.id.textViewTimeDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            texManagerName = itemView.findViewById(R.id.texManagerName);
            texDuration = itemView.findViewById(R.id.texDuration);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
