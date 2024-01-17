package com.example.glamlooksapp.utils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.callback.UserFetchCallback;
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
    public static final String Manager_TABLE = "Mangers";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private AuthCallBack authCallBack;

    private UserFetchCallback userFetchCallback;

    private UserCallBack userCallBack;



    public Database(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }

    public void setUserCallBack(UserCallBack userCallBack){
        this.userCallBack = userCallBack;
    }

    public void setUserFetchCallback(UserFetchCallback userFetchCallback){
        this.userFetchCallback = userFetchCallback;
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

    public void createAccount(String email, String password, User customerData) {
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

    public void saveUserData(User customer){
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

    public void updateUser(User user){
        this.db.collection(USERS_TABLE).document(user.getKey()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userCallBack.onUpdateComplete(task);
                    }
                });
    }

    public void fetchUserData(String uid){
        db.collection(USERS_TABLE).document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User user = value.toObject(User.class);
                userCallBack.onUserFetchDataComplete(user);
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
