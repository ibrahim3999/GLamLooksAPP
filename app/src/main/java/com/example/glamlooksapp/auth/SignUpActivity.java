package com.example.glamlooksapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Service;
import com.example.glamlooksapp.utils.Customer;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;


public class SignUpActivity extends AppCompatActivity {
    private static final int ACCOUNT_TYPE_CUSTOMER = 0;
    private static final int ACCOUNT_TYPE_MANAGER = 1;

    EditText firstName, lastName, email, password, phoneNumber;
    TextView loginRedirectBtn;
    EditText serviceName, ServicePrice, serviceDuration;
    Button signupBtn;
    ImageButton backBtn;
    private ProgressBar signup_PB_loading;
    private CheckBox showAdditionalInfoCheckbox;
    private Database database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        initVars();
    }

    private void findViews() {
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        phoneNumber = findViewById(R.id.editTextPhoneNumber);
        signup_PB_loading = findViewById(R.id.signup_PB_loading);
        loginRedirectBtn = findViewById(R.id.loginRedirectText_ls);
        serviceName = findViewById(R.id.serviceName);
        ServicePrice = findViewById(R.id.CostService);
        serviceDuration = findViewById(R.id.Duration);
        signupBtn = findViewById(R.id.signupButton);
        backBtn = findViewById(R.id.customBackButton);
        showAdditionalInfoCheckbox = findViewById(R.id.showAdditionalInfoCheckbox);
    }

    private void initVars() {
        database = new Database();
        database.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {}
            @Override
            public void onCreateAccountComplete(boolean status, String err) {
                signup_PB_loading.setVisibility(View.INVISIBLE);
                if (status) {
                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });
        showAdditionalInfoCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showAdditionalInfoCheckbox.isChecked()) {
                    serviceName.setVisibility(View.VISIBLE);
                    ServicePrice.setVisibility(View.VISIBLE);
                    serviceDuration.setVisibility(View.VISIBLE);
                } else {
                    serviceName.setVisibility(View.GONE);
                    ServicePrice.setVisibility(View.GONE);
                    serviceDuration.setVisibility(View.GONE);
                }
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    Toast.makeText(SignUpActivity.this, "Error CheckInput", Toast.LENGTH_LONG).show();
                    return;
                }
                signup_PB_loading.setVisibility(View.VISIBLE);
                Customer customer = null;
                Manager manager = null;
                String password = "";
                if (showAdditionalInfoCheckbox.isChecked()) {
                    manager = new Manager();

                    manager.setEmail(email.getText().toString());
                    manager.setFirstname(firstName.getText().toString());
                    manager.setLastname(lastName.getText().toString());
                    manager.setPhoneNumber(phoneNumber.getText().toString());
                    password = SignUpActivity.this.password.getText().toString().trim();
                    manager.setAccount_type(ACCOUNT_TYPE_MANAGER);
                    Service service = new Service(serviceName.getText().toString(),
                            Double.valueOf(ServicePrice.getText().toString()),
                            serviceDuration.getText().toString());
                    manager.setService(service);

                }else{
                    customer = new Customer();
                    customer.setEmail(email.getText().toString());
                    customer.setFirstname(firstName.getText().toString());
                    customer.setLastname(lastName.getText().toString());
                    customer.setPhoneNumber(phoneNumber.getText().toString());
                    password = SignUpActivity.this.password.getText().toString().trim();
                    customer.setAccount_type(ACCOUNT_TYPE_CUSTOMER);

                }
                if(customer !=null) {
                    Log.d("createAccount", "customer");
                    database.createAccount(customer.getEmail().trim(), password, customer);
                }
                if(manager!=null){
                    Log.d("createAccount", "manager");
                    database.createAccount(manager.getEmail().trim(), password, manager);
                }
            }
        });

        loginRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                Log.d("SignUpActivity", "Redirecting to login");
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                database.logout();
                finish();
            }
        });
    }

    private boolean checkInput() {
        Customer customer = new Customer();
        customer.setEmail(email.getText().toString());
        customer.setFirstname(firstName.getText().toString());
        customer.setLastname(lastName.getText().toString());
        customer.setPhoneNumber(phoneNumber.getText().toString());
        customer.setPassword(password.getText().toString());
        String password = customer.getPassword();
        String confirmPassword = customer.getPassword();

        if (!customer.isValid()) {
            Toast.makeText(SignUpActivity.this, "Please fill all user info!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 8) {
            Toast.makeText(SignUpActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Mismatch between password and confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
