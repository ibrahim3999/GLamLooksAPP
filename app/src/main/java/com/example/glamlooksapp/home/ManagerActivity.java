package com.example.glamlooksapp.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.auth.LoginActivity;
import com.example.glamlooksapp.utils.Database;

public class ManagerActivity extends AppCompatActivity {

    Button back_btn ;

    Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        back_btn  = findViewById(R.id.btnBackManger_Login);
        database = new Database();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.logout(); /// ????? NOTE!!!! Should change ....
                Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}