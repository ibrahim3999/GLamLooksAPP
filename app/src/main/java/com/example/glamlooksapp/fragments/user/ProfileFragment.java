package com.example.glamlooksapp.fragments.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.home.UpdateProfileActivity;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    public static String USER_INTENT_KEY = "CUSTOMER";
    private CircleImageView profileImage;
    private TextView profileName;
    private CardView editDetailsBtn;
    private CardView logoutBtn;
    private Customer currentCustomer;
    private Database database;
    private AppCompatActivity activity;

    public ProfileFragment() {}

    public ProfileFragment(AppCompatActivity activity) {
        // Required empty public constructor
        database = new Database();
        this.activity = activity;
    }

    public void setCurrentUser(Customer customer){
        this.currentCustomer = customer;
        displayUser(customer);
    }

    private void displayUser(Customer customer) {
        profileName.setText(customer.getFirstname());
        if(customer.getImageUrl() != null){
            // set image profile
            Glide
                    .with(activity)
                    .load(customer.getImageUrl())
                    .centerCrop()
                    .into(profileImage);
        }
    }

    private void findViews(View view) {
        logoutBtn = view.findViewById(R.id.fProfile_CV_logoutC);
        profileImage = view.findViewById(R.id.fProfile_IV_profileImageC);
        profileName = view.findViewById(R.id.fProfile_TV_nameC);
        editDetailsBtn = view.findViewById(R.id.fProfile_CV_editDetailsC);
        database.fetchUserData(database.getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_c, container, false);
        findViews(view);
        initVars();
        Log.d("ProfileFragment", "onCreateView");
        return view;
    }

    private void initVars() {
        database.setUserCallBack(new UserCallBack() {
            @Override
            public void onManagerFetchDataComplete(Manager manager) {
            }

            @Override
            public void onCustomerFetchDataComplete(Customer customer) {
                setCurrentUser(customer);
            }

            @Override
            public void onUpdateComplete(Task<Void> task) {
            }

            @Override
            public void onDeleteComplete(Task<Void> task) {
            }
        });
        editDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    Intent intent = new Intent(activity, UpdateProfileActivity.class);
                    intent.putExtra(USER_INTENT_KEY, currentCustomer);
                    startActivity(intent);

                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    Log.d("RemoveCustomer", database.getCurrentUser().getUid());

                    database.removeUser(database.getCurrentUser().getUid());
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}