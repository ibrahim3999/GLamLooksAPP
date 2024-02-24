package com.example.glamlooksapp.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.home.CustomerActivity;
import com.example.glamlooksapp.home.ManagerActivity;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn;
    private Button gmailBtn;
    TextView signupRedirectBtn;
    TextView forgotPassword;
    private Database database;
    private GoogleSignInClient client;
    private ProgressBar login_PB_loading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findV();
        initVars();
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("974118326374-12kl38t0b41ae8f76erb5jd4bn269jf0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);
    }

    private void findV() {
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_button);
        signupRedirectBtn = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgotPassword);
        gmailBtn = findViewById(R.id.login_gmail_btn);
        login_PB_loading = findViewById(R.id.login_PB_loading);
    }

    private void initVars() {
        database = new Database();
        database.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {
                login_PB_loading.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (database.getCurrentUser() != null) {
                        // Fetch user data
                        Log.d("LoginActivity", "User logged in successfully");
                        String uid = database.getCurrentUser().getUid();
                        database.fetchUserData(uid);
                    } else {
                        // Handle the case where login failed
                        Log.d("LoginActivity", "User login failed");
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(LoginActivity.this,"Success Login",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("LoginActivity", "User login failed");
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCreateAccountComplete(boolean status, String err) {}
        });


        database.setUserCallBack(new UserCallBack() {
            @Override
            public void onManagerFetchDataComplete(Manager manager) {}
            @Override
            public void onCustomerFetchDataComplete(Customer customer) {
                if(customer.getDeleted() == 1){
                    Toast.makeText(LoginActivity.this,"Account has been deleted",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (customer !=null) {
                    if(customer.getAccount_type() == 0) {
                        Toast.makeText(LoginActivity.this,"Hello " + customer.getFirstname(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"Hello " + customer.getFirstname(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ManagerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onUpdateComplete(Task<Void> task) {}
            @Override
            public void onDeleteComplete(Task<Void> task) {}
        });

        signupRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = LoginActivity.this.email.getText().toString().trim();
                String password = LoginActivity.this.password.getText().toString().trim();
                login_PB_loading.setVisibility(View.VISIBLE);
                if(email.isEmpty() ){
                    Log.d("LoginActivity", "Email is empty");
                    Toast.makeText(LoginActivity.this,"request email",Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Log.d("LoginActivity", "Password is empty");
                    Toast.makeText(LoginActivity.this,"request password",Toast.LENGTH_SHORT).show();
                }
                else {
                    // Perform login
                    Log.d("LoginActivity", "Performing login");
                    database.loginUser(email, password);
                }
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
                Log.d("LoginActivity", "Redirecting to ForgotPassword activity");
                finish();
            }
        });
        gmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent ,1234);
            }
        });
        if(database.getCurrentUser() != null){
            String uid = database.getCurrentUser().getUid();
            database.fetchUserData(uid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(getApplicationContext(),CustomerActivity.class);
                                    startActivity(intent);
                                    Log.d("GoogleSignIn", "User signed in with Google");
                                    finish();

                                }else{
                                    Log.e("GoogleSignIn", "Error signing in with Google: " + task.getException().getMessage());
                                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                int statusCode = e.getStatusCode();
                Log.e("GoogleSignIn", "Error signing in with Google. Status code: " + statusCode);
                Toast.makeText(this, "Error signing in with Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}