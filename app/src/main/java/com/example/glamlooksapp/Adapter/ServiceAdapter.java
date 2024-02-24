package com.example.glamlooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.OnTextViewClickListener;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Service;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private Context context;
    private ArrayList<Manager> managerArrayList;
    private OnTextViewClickListener onTextViewClickListener;

    public ServiceAdapter(Context context, ArrayList<Manager> managerArrayList, OnTextViewClickListener listener) {
        this.context = context;
        this.managerArrayList = managerArrayList;
        this.onTextViewClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manager_component, parent, false);
        Log.d("ServiceAdapter", "onCreateViewHolder: " + managerArrayList.size());
        return new ServiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Manager manager = managerArrayList.get(position);
        Service service = manager.getService();

        if (service != null) {
            String serviceName = service.getServiceName();
            holder.serviceName.setTextColor(Color.BLUE);
            holder.FirstName.setText(manager.getFirstname().trim() + " " + manager.getLastname().trim());
            holder.serviceName.setText(serviceName.trim().toLowerCase());
            holder.ServiceDuration.setText("Duration: " + manager.getService().getDuration() + " Minutes");
            holder.ServicePrice.setText("Price: " + Double.toString(manager.getService().getPrice()) + " ILS");

            holder.serviceName.setOnClickListener(v -> {
                if (onTextViewClickListener != null) {
                    onTextViewClickListener.onTextViewClicked(position, manager.getKey()); // Pass manager ID to listener
                }
            });
        } else {
            // Handle the case where the Service object is null
            Log.d("ServiceAdapter", "Service Not Available");
        }
    }

    @Override
    public int getItemCount() {
        return managerArrayList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView FirstName;
        TextView ServiceDuration;
        TextView ServicePrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.textViewServiceName);
            FirstName = itemView.findViewById(R.id.textViewFirstName);
            ServiceDuration = itemView.findViewById(R.id.textViewServiceDuration);
            ServicePrice = itemView.findViewById(R.id.textViewServicePrice);
        }
    }
}
