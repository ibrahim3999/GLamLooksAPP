package com.example.glamlooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.OnTextViewClickListener;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Service;
import com.example.glamlooksapp.utils.User;

import java.util.ArrayList;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private Context context;
    private ArrayList<Manager> manager_ArrayList;
    Database database = new Database();
    private OnTextViewClickListener onTextViewClickListener;

    public ServiceAdapter(Context context, ArrayList<Manager> maangerArrayList, OnTextViewClickListener listener) {
        this.context = context;
        this.manager_ArrayList = maangerArrayList;
        this.onTextViewClickListener = listener;
    }


    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manager_component, parent, false);
        return new ServiceViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Manager manager = manager_ArrayList.get(position);
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
                    onTextViewClickListener.onTextViewClicked(position);
                }
            });

//            holder.callBtn.setOnClickListener(v -> {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                String phoneNumber = holder.textViewPhoneNumber.getText().toString();
//                intent.setData(Uri.parse("tel:" + manager.getPhoneNumber()));
//                context.startActivity(intent);
//            });

        } else {
            // Handle the case where the Service object is null
            holder.textViewServiceName.setText("Service Not Available");
        }


    }





        @Override
        public int getItemCount () {
            return manager_ArrayList.size();
        }

        static class ServiceViewHolder extends RecyclerView.ViewHolder {
            TextView textViewServiceName;
            TextView textViewFirstName;
            TextView textViewLastName;
            TextView textViewPhoneNumber;

            TextView textViewServiceDuration;

            TextView textViewServicePrice;


            public ServiceViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
                textViewFirstName = itemView.findViewById(R.id.textViewFirstName);
                textViewLastName = itemView.findViewById(R.id.textViewLastName);
                textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
                textViewServiceDuration = itemView.findViewById(R.id.textViewServiceDuration);
                textViewServicePrice = itemView.findViewById(R.id.textViewServicePrice);


            }
        }

    }


