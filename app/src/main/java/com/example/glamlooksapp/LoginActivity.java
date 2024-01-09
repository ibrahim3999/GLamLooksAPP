package com.example.glamlooksapp;

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
import android.widget.TextView;
import android.widget.Toast;
import com.example.glamlooksapp.SignUpActivity;
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
        findCustomers();
        initVars();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("974118326374-uittcf3tmoacmqpokt4b3qnqarq7619p.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);

    }

    private void findCustomers() {
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
                loginButton.setVisibility((View.INVISIBLE));
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
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

//    private void signInByGmail() {
//        gsc.signOut(); // Sign out previous user
//        Intent intent = gsc.getSignInIntent();
//        startActivityForResult(intent, 100);
//    }

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
