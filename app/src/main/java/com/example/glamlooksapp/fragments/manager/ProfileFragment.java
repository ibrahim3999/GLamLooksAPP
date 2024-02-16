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
import com.example.glamlooksapp.Adapter.CustomersAdapter;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.home.UpdateProfileActivity;
import com.example.glamlooksapp.home.UpdateProfileActivityM;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    public static String USER_INTENT_KEY = "CUSTOMER";
    private CircleImageView fProfile_IV_profileImage;
    private TextView fProfile_TV_name;
    private CardView fProfile_CV_editDetails;
    private CardView fProfile_CV_logout;
    private Manager currentUser ;
    private Database database;
    private AppCompatActivity activity;



    public ProfileFragment() {

    }

    public ProfileFragment(AppCompatActivity activity) {
        // Required empty public constructor
//        database = new Database();
        this.activity = activity;
    }

    public void setCurrentUser(Manager user){
        this.currentUser = user;
        displayUser(user);
    }

    private void displayUser(Manager manager) {
        fProfile_TV_name.setText(manager.getFirstname());
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

    private void initVars() {
        database.setUserCallBack(new UserCallBack() {



            @Override
            public void onUserFetchDataComplete(Manager manager) {
                setCurrentUser(manager);
            }

            @Override
            public void onUserFetchDataComplete(User user) {

            }

            @Override
            public void onUpdateComplete(Task<Void> task) {

            }

            @Override
            public void onDeleteComplete(Task<Void> task) {

            }
        });


        fProfile_CV_editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(activity != null) {
                    Intent intent = new Intent(activity, UpdateProfileActivityM.class);
                    intent.putExtra(USER_INTENT_KEY, currentUser);
                    startActivity(intent);

                }
            }
        });

        fProfile_CV_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    database.logout();
//                    activity.finish();
                }
            }
        });


    }

    private void findViews(View view) {
        fProfile_CV_logout = view.findViewById(R.id.fProfile_CV_logout);
        fProfile_IV_profileImage = view.findViewById(R.id.fProfile_IV_profileImage);
        fProfile_TV_name = view.findViewById(R.id.fProfile_TV_name);
        fProfile_CV_editDetails = view.findViewById(R.id.fProfile_CV_editDetails);
        database.fetchManagerData(database.getCurrentUser().getUid());
    }

}