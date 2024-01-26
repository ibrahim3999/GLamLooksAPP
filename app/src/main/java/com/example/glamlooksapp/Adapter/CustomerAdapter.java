package com.example.glamlooksapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.utils.User;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<User> userList;

    public CustomerAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User user = userList.get(position);

        holder.textViewCustomerName.setText(user.getFirstname());
        holder.textViewPN.setText(String.valueOf(user.getPhoneNumber()));
        holder.textViewLN.setText(String.valueOf(user.getLastname()));

        // You need to load the actual image using a library like Glide
        Glide.with(context)
                .load(user.getImageUrl()) // Replace with the actual image URL or resource
                .placeholder(R.drawable.upload)
                .into(holder.imageViewCustomer);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCustomer;
        TextView textViewCustomerName;
        TextView textViewPN;
        TextView textViewLN;


        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCustomer = itemView.findViewById(R.id.imageViewCustomer);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewPN = itemView.findViewById(R.id.textViewPN);
            textViewLN = itemView.findViewById(R.id.textViewLN);

        }
    }
}
