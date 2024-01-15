package com.example.glamlooksapp.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.fragments.user.AboutUFragment;
import com.example.glamlooksapp.fragments.user.HomeFragment;
import com.example.glamlooksapp.fragments.user.SettingsUFragment;
import com.example.glamlooksapp.fragments.user.ShareUFragment;
import com.example.glamlooksapp.utils.Database;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;



import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class CustomerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    Button googleAuth;

    Database database;

    GoogleSignInClient mGoogleSignInClient;

    int RC_SIGN_IN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        googleAuth = findViewById(R.id.btnGoogleAuth);

        database = new Database();

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("974118326374-uittcf3tmoacmqpokt4b3qnqarq7619p.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
                googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }



}