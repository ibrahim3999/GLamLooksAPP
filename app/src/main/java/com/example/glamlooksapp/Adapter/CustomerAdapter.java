package com.example.glamlooksapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.utils.Datetime;
import com.example.glamlooksapp.utils.User;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<User> customerList;

    public CustomerAdapter(Context context, List<User> customerList) {
        this.context = context;
        this.customerList = customerList;
    }


    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customerList.get(position);
        String fullName = customer.getFirstname()+ " " + customer.getLastname();
        holder.textViewCustomerName.setText(fullName);
        holder.textViewPN.setText(String.valueOf(customer.getPhoneNumber()));
        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an Intent with ACTION_DIAL to launch the phone app
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String phoneNumber = holder.textViewPN.getText().toString();
                // Set the data (phone number) for the Intent
                intent.setData(Uri.parse("tel:" + customer.getPhoneNumber()));

                // Start the activity with the Intent
                context.startActivity(intent);
            }
        });


        // You need to load the actual image using a library like Glide
        Glide.with(context)
                .load(customer.getImageUrl()) // Replace with the actual image URL or resource
                .placeholder(R.drawable.upload)
                .into(holder.imageViewCustomer);

    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCustomer;
        TextView textViewCustomerName;
        TextView textViewPN;


        Button callBtn;


        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCustomer = itemView.findViewById(R.id.imageViewCustomer);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewPN = itemView.findViewById(R.id.textViewPN);
            callBtn = itemView.findViewById(R.id.callButton);
        }
    }
}
