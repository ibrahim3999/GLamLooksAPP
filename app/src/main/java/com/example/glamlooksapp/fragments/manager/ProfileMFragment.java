package com.example.glamlooksapp.fragments.manager;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.glamlooksapp.home.UpdateProfileActivityM;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Customer;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileMFragment extends Fragment {
    public static String USER_INTENT_KEY = "CUSTOMER";
    private CircleImageView fProfile_IV_profileImage;
    private TextView profileName;
    private CardView profileEditDetails;
    private CardView profileLogout;
    private Manager currentUser;
    private Database database;
    private AppCompatActivity activity;

    public ProfileMFragment() {}

    public ProfileMFragment(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setCurrentUser(Manager user){
        this.currentUser = user;
        displayUser(user);
    }

    private void displayUser(Manager manager) {
        profileName.setText(manager.getFirstname());
        if(manager.getImageUrl() != null){
            // set image profile
            Glide
                    .with(activity)
                    .load(manager.getImageUrl())
                    .centerCrop()
                    .into(fProfile_IV_profileImage);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = new Database();
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        profileLogout = view.findViewById(R.id.fProfile_CV_logout);
        fProfile_IV_profileImage = view.findViewById(R.id.fProfile_IV_profileImage);
        profileName = view.findViewById(R.id.fProfile_TV_name);
        profileEditDetails = view.findViewById(R.id.fProfile_CV_editDetails);
        database.fetchManagerData(database.getCurrentUser().getUid());
    }

    private void initVars() {
        database.setUserCallBack(new UserCallBack() {
            @Override
            public void onManagerFetchDataComplete(Manager manager) {
                setCurrentUser(manager);
            }

            @Override
            public void onCustomerFetchDataComplete(Customer customer) {
            }

            @Override
            public void onUpdateComplete(Task<Void> task) {
            }

            @Override
            public void onDeleteComplete(Task<Void> task) {
            }
        });
        profileEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activity != null) {
                    Intent intent = new Intent(activity, UpdateProfileActivityM.class);
                    intent.putExtra(USER_INTENT_KEY, currentUser);
                    startActivity(intent);

                }
            }
        });
        profileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    database.logout();
                }
            }
        });
    }

}