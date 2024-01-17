package com.example.glamlooksapp.fragments.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.home.UpdateProfileActivity;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.User;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    public static String USER_INTENT_KEY = "CUSTOMER";
    private CircleImageView fProfile_IV_profileImage;
    private TextView fProfile_TV_name;
    private CardView fProfile_CV_editDetails;
    private CardView fProfile_CV_logout;
    private User currentUser ;
    private Database database;
    private AppCompatActivity activity;

    public ProfileFragment() {

    }

    public ProfileFragment(AppCompatActivity activity) {
        // Required empty public constructor
        database = new Database();
        this.activity = new AppCompatActivity();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        displayUser(user);
    }

    private void displayUser(User user) {
        fProfile_TV_name.setText(user.getFirstname());
//        if(user.getImageUrl() != null){
//            // set image profile
//            Glide
//                    .with(activity)
//                    .load(user.getImageUrl())
//                    .centerCrop()
//                    .into(fProfile_IV_profileImage);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void initVars() {
        fProfile_CV_editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, UpdateProfileActivity.class);
                intent.putExtra(USER_INTENT_KEY, currentUser);
                startActivity(intent);

            }
        });

        fProfile_CV_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                database.logout();
                activity.finish();
            }
        });
    }

    private void findViews(View view) {
        fProfile_CV_logout = view.findViewById(R.id.fProfile_CV_logout);
        fProfile_IV_profileImage = view.findViewById(R.id.fProfile_IV_profileImage);
        fProfile_TV_name = view.findViewById(R.id.fProfile_TV_name);
        fProfile_CV_editDetails = view.findViewById(R.id.fProfile_CV_editDetails);
    }

}