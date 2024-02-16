package com.example.glamlooksapp.home;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.databinding.ActivityCustomerBinding;
import com.example.glamlooksapp.fragments.manager.TimesFragment;
import com.example.glamlooksapp.fragments.user.AboutUFragment;
import com.example.glamlooksapp.fragments.user.HomeFragment;
import com.example.glamlooksapp.fragments.user.ProductsFragment;
import com.example.glamlooksapp.fragments.user.ProfileFragmentC;
import com.example.glamlooksapp.fragments.user.TimesCusFragment;
import com.example.glamlooksapp.utils.Database;
import androidx.fragment.app.FragmentTransaction;


public class CustomerActivity extends AppCompatActivity {


    private Database database;
    private ActivityCustomerBinding customer_Binding;

    public CustomerActivity (){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        database = new Database();
        customer_Binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(customer_Binding.getRoot());
        replaceFragment(new HomeFragment(CustomerActivity.this));


        customer_Binding.bottomNavigationC.setOnItemSelectedListener(item->
        {
            switch (item.getItemId()){
                case R.id.navigation_homeC:
                    replaceFragment(new HomeFragment(CustomerActivity.this));
                    break;

                case R.id.navigation_about:
                    replaceFragment(new AboutUFragment());
                    break;

                case R.id.profileC:
                    replaceFragment(new ProfileFragmentC(CustomerActivity.this));
                    Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                    break;

                case R.id.products:
                    replaceFragment(new ProductsFragment());
                    Toast.makeText(this,"Products",Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });




    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.times:
                Toast.makeText(this,"Times",Toast.LENGTH_SHORT).show();
                replaceFragment(new TimesCusFragment(CustomerActivity.this));
                return true;

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

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerC,fragment);
        fragmentTransaction.commit();
    }



}