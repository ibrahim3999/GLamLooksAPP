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
        return new ServiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Manager manager = managerArrayList.get(position);
        Service service = manager.getService();

        if (service != null) {
            String serviceName = service.getServiceName();
            holder.textViewServiceName.setTextColor(Color.BLUE);
            holder.textViewFirstName.setText(manager.getFirstname());
            holder.textViewLastName.setText(manager.getLastname());
            holder.textViewServiceName.setText(serviceName);
            holder.textViewServiceDuration.setText(manager.getService().getDuration());
            holder.textViewServicePrice.setText(Double.toString(manager.getService().getPrice()));

            holder.textViewServiceName.setOnClickListener(v -> {
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
        TextView textViewServiceName;
        TextView textViewFirstName;
        TextView textViewLastName;
        TextView textViewServiceDuration;
        TextView textViewServicePrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewFirstName = itemView.findViewById(R.id.textViewFirstName);
            textViewLastName = itemView.findViewById(R.id.textViewLastName);
            textViewServiceDuration = itemView.findViewById(R.id.textViewServiceDuration);
            textViewServicePrice = itemView.findViewById(R.id.textViewServicePrice);
        }
    }
}
