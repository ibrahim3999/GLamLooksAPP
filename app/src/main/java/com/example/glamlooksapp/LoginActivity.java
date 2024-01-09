package com.example.glamlooksapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.glamlooksapp.SignUpActivity;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Api;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity  {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private Button gmail_button;
    TextView login_BTN_signup;

    TextView forgotPassword;
    private Database database;

    GoogleSignInClient gsc;

    GoogleSignInOptions gso;






    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findCustomers();
        initVars();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this,gso);


    }

    private void findCustomers() {
        emailEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        login_BTN_signup = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgotPassword);
        gmail_button = findViewById(R.id.login_gmail_btn);
    }

    private void initVars(){

        database = new Database();
        database.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {
                loginButton.setVisibility((View.INVISIBLE));
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(LoginActivity.this,error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCreateAccountComplete(boolean status, String err) {

            }
        });

        login_BTN_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                database.loginUser(email, password);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        gmail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByGmail();  // Corrected method name
            }
        });




    }

   private void signInByGmail(){
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // You can access account information here if needed.
                // For example: String email = account.getEmail();
                CustomerActivity();
            } catch (ApiException e) {
                int statusCode = e.getStatusCode();
                Log.e("GoogleSignIn", "Error signing in with Google. Status code: " + statusCode);
                Toast.makeText(this, "Error signing in with Google", Toast.LENGTH_SHORT).show();
            }


        }
    }


    private void CustomerActivity(){
        finish();
        Intent intent = new Intent(getApplicationContext(),CustomerActivity.class);
        startActivity(intent);
        finish();
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//        Database db = new Database();
//        if(db.getCurrentUser() != null){
//            startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
//            finish();
//        }
//    }


}

