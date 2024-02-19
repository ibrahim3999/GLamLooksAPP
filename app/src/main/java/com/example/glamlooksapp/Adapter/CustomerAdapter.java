package com.example.glamlooksapp.Adapter;

import android.annotation.SuppressLint;
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

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private ArrayList<User> customerList;
    Database database = new Database();

    public CustomerAdapter(Context context, ArrayList<User> customerList) {
        this.context = context;
        this.customerList = customerList;
    }


    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customerList.get(position);
        String fullName = customer.getFirstname() + " " + customer.getLastname();
        holder.textViewCustomerName.setText(fullName);
        holder.textViewPN.setText(String.valueOf(customer.getPhoneNumber()));
        Datetime datetime = customer.getDateTime();
        holder.textViewTime.setText(datetime.toString());

        holder.callBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            String phoneNumber = holder.textViewPN.getText().toString();
            intent.setData(Uri.parse("tel:" + customer.getPhoneNumber()));
            context.startActivity(intent);
        });

        holder.removeButton.setOnClickListener(v -> {
            String datetimeUid = datetime.getUUid();

            database.deleteUserTime(datetimeUid);

            customerList.remove(position);
            notifyDataSetChanged();
        });

        Glide.with(context)
                .load(customer.getImageUrl())
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
        TextView textViewPN, textViewTime;
        Button callBtn;
        ImageButton removeButton;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCustomer = itemView.findViewById(R.id.imageViewCustomer);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewPN = itemView.findViewById(R.id.textViewPN);
            callBtn = itemView.findViewById(R.id.callButton);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            removeButton = itemView.findViewById(R.id.removeButton);

        }
    }
}
