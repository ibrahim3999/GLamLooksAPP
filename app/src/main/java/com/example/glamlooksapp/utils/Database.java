package com.example.glamlooksapp.utils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.glamlooksapp.callback.AuthCallBack;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class Database {


    public static final String USERS_TABLE = "Customers";

    private FirebaseAuth mAuth;
    private AuthCallBack authCallBack;
    private CustomerCallBack customerCallBack;
//    private IdeaCallBack ideaCallBack;
//    private CategoryCallBack categoryCallBack;
    private FirebaseFirestore db;


    public Database(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }

    public void setUserCallBack(CustomerCallBack userCallBack){
        this.customerCallBack = userCallBack;
    }

    public void loginUser(String email, String password){
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authCallBack.onLoginComplete(task);
                    }
                });
    }

    public void createAccount(String email, String password, Customer customerData) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(getCurrentUser() != null){
                            customerData.setKey(getCurrentUser().getUid());
                            saveUserData(customerData);
                        }else {
                            authCallBack.onCreateAccountComplete(false, Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    public void saveUserData(Customer customer){
        this.db.collection(USERS_TABLE).document(customer.getKey()).set(customer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            authCallBack.onCreateAccountComplete(true, "");
                        else
                            authCallBack.onCreateAccountComplete(false, Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void updateUser(Customer customer){
        this.db.collection(USERS_TABLE).document(customer.getKey()).set(customer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        customerCallBack.onUpdateComplete(task);
                    }
                });
    }

    public void fetchUserData(String uid){
        db.collection(USERS_TABLE).document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                Customer customer = value.toObject(Customer.class);
                customerCallBack.onUserFetchDataComplete(customer);
            }
        });
    }

    public FirebaseUser getCurrentUser(){

        return mAuth.getCurrentUser();
    }


    public void logout() {
        this.mAuth.signOut();
    }


}
