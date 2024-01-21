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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;

import com.example.glamlooksapp.R;

import com.example.glamlooksapp.auth.LoginActivity;
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








        database =new Database();
        managerBinding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(managerBinding.getRoot());


        managerBinding.bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.settings:
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.exit:
                Toast.makeText(this,"Exit",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                database.logout();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init_home_frgment(int id , androidx.fragment.app.Fragment fragment,String tag){

    }

}