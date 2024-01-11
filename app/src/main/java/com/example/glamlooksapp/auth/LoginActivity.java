package com.example.glamlooksapp.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.home.CustomerActivity;
import com.example.glamlooksapp.home.ManagerActivity;
import com.example.glamlooksapp.utils.Customer;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private Button gmail_button;
    TextView login_BTN_signup;
    TextView forgotPassword;
    private Database database;
    private GoogleSignInClient client;

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
        emailEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        login_BTN_signup = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgotPassword);
        gmail_button = findViewById(R.id.login_gmail_btn);
    }

    private void initVars() {
        database = new Database();


        database.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {
                loginButton.setVisibility((View.VISIBLE));
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,"Success Login",Toast.LENGTH_SHORT).show();
                } else {
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCreateAccountComplete(boolean status, String err) {

            }
        });

        database.setUserCallBack(new CustomerCallBack() {
            @Override
            public void onUserFetchDataComplete(Customer customer) {
                if (customer!=null) {
                    int type = customer.getAccount_type();
                    if(type==1) {
                        Toast.makeText(LoginActivity.this,"here1",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"here2",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ManagerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onUpdateComplete(Task<Void> task) {

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

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//                String uid = database.getCurrentUser().getUid();
//                database.loginUser(email, password);
//
//                database.fetchUserData(uid);
//            }
//        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Perform login
                database.loginUser(email, password);

                // Introduce a delay before fetching user data
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check if the login was successful
                        if (database.getCurrentUser() != null) {
                            // Fetch user data
                            String uid = database.getCurrentUser().getUid();
                            database.fetchUserData(uid);
                        } else {
                            // Handle the case where login failed
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000); // 2000 milliseconds (adjust as needed)
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        gmail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = client.getSignInIntent();
               startActivityForResult(intent ,1234);
            }
        });
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

                                        }else{
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){
            Intent intent = new Intent(this,CustomerActivity.class);
            startActivity(intent);
        }
    }
}
