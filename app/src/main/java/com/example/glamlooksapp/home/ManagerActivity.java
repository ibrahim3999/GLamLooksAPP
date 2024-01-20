package com.example.glamlooksapp.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.databinding.ActivityManagerBinding;
import com.example.glamlooksapp.fragments.manager.HomeMFragment;
import com.example.glamlooksapp.fragments.manager.LogoutMFragment;
import com.example.glamlooksapp.fragments.manager.ProfileFragment;
import com.example.glamlooksapp.fragments.user.AboutUFragment;
import com.example.glamlooksapp.fragments.user.HomeFragment;
import com.example.glamlooksapp.utils.Database;

public class ManagerActivity extends AppCompatActivity {


    Database database;

    ActivityManagerBinding binding;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        database = new Database();
        binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeMFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item->
        {
            switch (item.getItemId()){
                case R.id.navigation_home:
                    replaceFragment(new HomeMFragment());
                    break;

                case R.id.navigation_about:
                    replaceFragment(new AboutUFragment());
                    break;

//                case R.id.logoutMenu:
//                Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
//                database.logout();
//                startActivity(intent);
//                finish();
//                break;

            }
            return true;
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.android:
                Toast.makeText(this,"Android",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.profile:
                Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                replaceFragment(new ProfileFragment(new AppCompatActivity()));
                return true;
            case R.id.settings:
                // Handle Settings menu item click
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exit:
                // Handle Exit menu item click
                Toast.makeText(this,"Exit",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();
    }
}