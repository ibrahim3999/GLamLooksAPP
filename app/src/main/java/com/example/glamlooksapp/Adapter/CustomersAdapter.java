package com.example.glamlooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomersViewHolder> {

    private Context context;
    private static  String TAG= "CustomersAdapter";
    private ArrayList<Datetime> datetimeArrayList;
    private Database database = new Database();

    public CustomersAdapter(Context context, ArrayList<Datetime> datetimeArrayList) {
        this.context = context;
        this.datetimeArrayList = datetimeArrayList;
    }

    public void removeItem(int position) {
        datetimeArrayList.remove(position);
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_item, parent, false);
        return new CustomersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Datetime datetime = datetimeArrayList.get(position);
        String serviceName = datetime.getServiceName() + " ";
        holder.textViewServName.setText(serviceName);
        holder.textViewTimeDate.setText(datetime.getFormattedDate());
        holder.textViewTime.setText(datetime.getFormattedTime());

        // Fetch manager's data by ID
        String managerId = datetime.getManagerId();
        Log.d("ManagerId", datetime.getManagerId());

        database.fetchManagerData(managerId, new UserCallBack() {
            @Override
            public void onUserFetchDataComplete(Manager manager) {
                if (manager != null) {
                    // Set manager's data in the ViewHolder
                    String manager_name = manager.getFirstname() + " " + manager.getLastname();
                    holder.texManagerName.setText(manager_name);
                    holder.texDuration.setText(manager.getService().getDuration());
                    Log.d("UserCallBack", "onUserFetchDataComplete is called1");

                    holder.removeButton.setOnClickListener(v -> {
                        String datetimeUid = datetime.getUUid();
                       // Log.d(TAG,datetime.getUUid());
                        database.deleteUserTime(datetimeUid, new UserCallBack() {
                            @Override
                            public void onUserFetchDataComplete(Manager manager) throws NoSuchAlgorithmException {

                            }

                            @Override
                            public void onUserFetchDataComplete(User user) {

                            }

                            @Override
                            public void onUpdateComplete(Task<Void> task) {

                            }

                            @Override
                            public void onDeleteComplete(Task<Void> task) {
                                if(task != null){
                                    Log.d(TAG,"Deleting an appointment queue");
                                }else {
                                    Log.e(TAG,"Falied Deleted");
                                }
                            }
                        });

                        datetimeArrayList.remove(position);
                        notifyDataSetChanged();
                    });

                } else {
                    Log.d("UserCallBack", "onUserFetchDataComplete is called400");
                }
                // Log message to indicate that onUserFetchDataComplete is called
                Log.d("UserCallBack", "onUserFetchDataComplete is called2");
            }

            @Override
            public void onUserFetchDataComplete(User user) {
                // Log message to indicate that onUserFetchDataComplete is called
                Log.d("UserCallBack", "onUserFetchDataComplete is called3");
            }

            @Override
            public void onUpdateComplete(Task<Void> task) {
                // Log message to indicate that onUpdateComplete is called
                Log.d("UserCallBack", "onUpdateComplete is called4");
            }

            @Override
            public void onDeleteComplete(Task<Void> task) {

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
