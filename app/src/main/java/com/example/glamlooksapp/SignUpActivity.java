package com.example.glamlooksapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glamlooksapp.callback.AuthCallBack;
import com.google.firebase.auth.AuthResult;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.Task;

public class SignUpActivity extends AppCompatActivity {

    EditText firstname, lastname, signupEmail, signupPassword, phoneNumber;
    TextView txtV_button_back;
    Button signupButton;
    ImageButton backButton;
//    FirebaseDatabase database;
//    DatabaseReference reference;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findCustomers();
        initVars_SP();
    }

    private void findCustomers(){
        firstname = findViewById(R.id.firstName);
        signupEmail = findViewById(R.id.signup_email);
        lastname = findViewById(R.id.lastName);
        signupPassword = findViewById(R.id.signup_password);
        txtV_button_back = findViewById(R.id.loginRedirectText_ls);
        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.customBackButton);
        phoneNumber = findViewById(R.id.editTextPhoneNumber);
    }

    private void initVars_SP(){
        database = new Database();
        database.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {

            }

            @Override
            public void onCreateAccountComplete(boolean status, String err) {
//                signup_PB_loading.setVisibility(View.INVISIBLE);
                if(status){
                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    database.logout();
                    finish();
                }else{
                    Toast.makeText(SignUpActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()){
                    Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_LONG).show();

                    return;
                }

                Toast.makeText(SignUpActivity.this, "success", Toast.LENGTH_SHORT).show();

                Customer customer = new Customer();
                customer.setEmail(signupEmail.getText().toString());
                customer.setFirstname(firstname.getText().toString());
                customer.setLastname(lastname.getText().toString());
                customer.setPhoneNumber(phoneNumber.getText().toString());

                String password = signupPassword.getText().toString().trim();
                database.createAccount(customer.getEmail(), password, customer);
            }
        });


        txtV_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkInput() {
        Customer customer = new Customer();
                customer.setEmail(signupEmail.getText().toString());
                customer.setFirstname(firstname.getText().toString());
                customer.setLastname(lastname.getText().toString());
                customer.setPhoneNumber(phoneNumber.getText().toString());
                customer.setPassword(signupPassword.getText().toString());


        String password = customer.getPassword();
        String confirmPassword = customer.getPassword();

        if(!customer.isValid()) {
            Toast.makeText(SignUpActivity.this, "Please fill all user info!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length() < 6){
            Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(SignUpActivity.this, "mismatch between password and confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



}
