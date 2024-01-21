package com.example.glamlooksapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;

import com.example.glamlooksapp.R;

import com.example.glamlooksapp.databinding.ActivityManagerBinding;
import com.example.glamlooksapp.fragments.user.HomeFragment;
import com.example.glamlooksapp.utils.Database;

import java.util.Calendar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CustomerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private CalendarView calendarView;

    private AlertDialog.Builder alertDialog;
    private Calendar calendar;
    private Database database;
    private ActivityManagerBinding managerBinding;

    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        init();
        // fragments








        database =new Database();
        managerBinding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(managerBinding.getRoot());


        managerBinding.bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.logoutMenu:
                    //Toast.makeText(CustomerActivity.this,"yes", Toast.LENGTH_SHORT).show();
                    // back to login activity
                    View back_loginView = LayoutInflater.from(CustomerActivity.this).inflate(R.layout.activity_login,null);
                    setContentView(back_loginView);
                    break;
                case R.id.navigation_home:
                    homeFragment = new HomeFragment();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(R.id.fragmentContainer,homeFragment,"homeFragment");
                    transaction.commit();

                    break;

            }

            return true;
        });
    }



    private void init() {
       // calendarView = findViewById(R.id.)
      //  calendarView.setShowWeekNumber(true);
      //  calendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        // fragments created

    }

    private void init_home_frgment(int id , androidx.fragment.app.Fragment fragment,String tag){

    }

}