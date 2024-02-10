package com.example.glamlooksapp.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.User;

import java.util.ArrayList;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomersViewHolder> {


    private Context context;
    private ArrayList<Datetime> datetimeArrayList;
    Database database = new Database();

    public CustomersAdapter(Context context, ArrayList<Datetime> datetimeArrayList) {
        this.context = context;
        this.datetimeArrayList = datetimeArrayList;
    }


    @NonNull
    @Override
    public CustomersAdapter.CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_item, parent, false);
        return new CustomersAdapter.CustomersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersAdapter.CustomersViewHolder holder, int position) {
        Datetime datetime = datetimeArrayList.get(position);
        String serviceName = datetime.getServiceName() + " " ;
        holder.textViewServiceName.setText(serviceName);
        holder.textViewTimeDate.setText(datetime.getFormattedDate());
        holder.textViewTime.setText(datetime.getFormattedTime());




    }

    @Override
    public int getItemCount() {
        return datetimeArrayList.size();
    }

    static class CustomersViewHolder extends RecyclerView.ViewHolder {
        TextView textViewServiceName;
        TextView textViewTimeDate,textViewTime;
        public CustomersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewTimeDate = itemView.findViewById(R.id.textViewTimeDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);


        }
    }


}
