package com.example.glamlooksapp.utils;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.ProductCallBack;
import com.example.glamlooksapp.callback.UserCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Objects;



public class Database {


    public static final String USERS_TABLE = "Customers";
    public static final String Manager_TABLE = "Managers";
    public static final String TIMES_TABLE = "TSchedule";

    public static final String MANAGER_UID = "Zpa8hiasUAShwkSfwr0GxHXBb5q2";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private AuthCallBack authCallBack;
    private ProductCallBack productCallBack;
    private CustomerCallBack customerCallBack;

//    private UserFetchCallback userFetchCallback;

    private UserCallBack userCallBack;

    public static final String CATEGORIES_TABLE = "Categories";
    public static final String PRODUCTS_TABLE = "Products";
    private ArrayList<Product> productList;  // Add this list to store products
    ArrayList<User> customersWantedList ;

    private ArrayList<String> listKeysDates;
    public Database(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        productList = new ArrayList<Product>();
        customersWantedList = new ArrayList<User>();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }

    public void setUserCallBack(UserCallBack userCallBack){
        this.userCallBack = userCallBack;
    }

//    public void setUserFetchCallback(UserFetchCallback userFetchCallback){
//        this.userFetchCallback = userFetchCallback;
//    }


    public void setProductCallBack(ProductCallBack productCallBack){
        this.productCallBack = productCallBack;
    }

    public void setCustomerCallBack(CustomerCallBack customerCallBack){
        this.customerCallBack = customerCallBack;
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


    public void fetchProducts() {
        db.collection(PRODUCTS_TABLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    return;
                }

                 productList = new ArrayList<>();

                for (QueryDocumentSnapshot document : value) {
                    Product product = document.toObject(Product.class);
                    productList.add(product);
                }

                productCallBack.onFetchProductsComplete(productList);
            }
        });
    }




    public void fetchUserDates() {

        db.collection(TIMES_TABLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                listKeysDates = new ArrayList<>();
                for (QueryDocumentSnapshot document : value) {
                    Datetime datetime = document.toObject(Datetime.class);
                    String uid = datetime.getKey();
                    listKeysDates.add(uid);
                }
                fetchUsersByKeys(listKeysDates);
            }
        });


    }

    private void fetchUsersByKeys(ArrayList<String> userKeys) {
        db.collection(USERS_TABLE)
                .whereIn("key", userKeys) //  "key" is the field name in the "users" collection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        for (QueryDocumentSnapshot document : value) {
                            User customer = document.toObject(User.class);
                            customersWantedList.add(customer);
                        }

                        customerCallBack.onFetchCustomerComplete(customersWantedList);
                    }
                });
    }



    public void createAccount(String email, String password, User userData) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(getCurrentUser() != null){

                            userData.setKey(getCurrentUser().getUid());
                            saveUserData(userData);
                        }else {
                            authCallBack.onCreateAccountComplete(false, Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    public void saveUserTimes(Datetime dateTime, User customer){
        this.db.collection(TIMES_TABLE).document(customer.getKey()).set(dateTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            customerCallBack.onAddICustomerComplete(task);
                        else
                            customerCallBack.onAddICustomerComplete(task);
                    }
                });
    }

    public void saveUserData(User user){
        this.db.collection(USERS_TABLE).document(user.getKey()).set(user)
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
                if(user.getImagePath() != null){
                    user.setImageUrl(downloadImageUrl(user.getImagePath()));
                }
                user.setKey(value.getId());
                userCallBack.onUserFetchDataComplete(user);
            }
        });
    }



    public void uploadProduct(Product product){
        this.db.collection(PRODUCTS_TABLE).document().set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        productCallBack.onAddIProductsComplete(task);
                    }
                });
    }



    public FirebaseUser getCurrentUser(){

        return mAuth.getCurrentUser();
    }


    public void logout() {
        this.mAuth.signOut();
    }

    public String downloadImageUrl(String imagePath){
      Task<Uri> task =  mStorage.getReference(imagePath).getDownloadUrl();
      while(!task.isComplete());
      return task.getResult().toString();
    }

    public boolean uploadImage(Uri selectedImageUri, String path) {
        UploadTask task =  this.mStorage.getReference(path).putFile(selectedImageUri);
        while(!task.isComplete());
        return task.isSuccessful();
    }



}