package com.example.glamlooksapp.utils;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.DatetimeCallback;
import com.example.glamlooksapp.callback.ProductCallBack;
import com.example.glamlooksapp.callback.UserCallBack;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class Database {


    public static final String USERS_TABLE = "Customers";
    public static final String Manager_TABLE = "Managers";
    public static final String TIMES_TABLE = "TSchedule";
    public static final String TIMES_TABLE_HAIRCUT = "TSchedule_haircut";
    public static final String TIMES_TABLE_NAILS = "TSchedule_nails";
    public static final String TIMES_TABLE_LASER = "TSchedule_laser";
    public static final String USERS_PROFILE_IMAGES = "Users/";

    public static final String MANAGER_UID = "Zpa8hiasUAShwkSfwr0GxHXBb5q2";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private AuthCallBack authCallBack;
    private ProductCallBack productCallBack;
    private CustomerCallBack customerCallBack;

    private UserCallBack userCallBack;

    public static final String CATEGORIES_TABLE = "Categories";
    public static final String PRODUCTS_TABLE = "Products";
    private ArrayList<Product> productList;  // Add this list to store products
    ArrayList<User> customersWantedList ;

    private ArrayList<String> listKeysDates;

    public ArrayList<Datetime> getList_dates() {
        return list_dates;
    }

    private ArrayList<Datetime> list_dates;

    public Database(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        productList = new ArrayList<Product>(1000);
        customersWantedList = new ArrayList<User>(1000);
        list_dates = new ArrayList<Datetime>();
        listKeysDates = new ArrayList<>();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }

    public void setUserCallBack(UserCallBack userCallBack){
        this.userCallBack = userCallBack;
    }


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
    public void fetchUserDates(DatetimeCallback datetimeCallback) {
        db.collection(TIMES_TABLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list_dates.clear(); // Clear the list before updating

                for (QueryDocumentSnapshot document : value) {
                    Datetime datetime = document.toObject(Datetime.class);
                    list_dates.add(datetime);
                    String uid = datetime.getKey();
                    listKeysDates.add(uid);
                }
                datetimeCallback.onDatetimeFetchComplete(list_dates);
            }
        });
    }

    public void fetchUserDates() {

        db.collection(TIMES_TABLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

//                listKeysDates = new ArrayList<>();
                assert value != null;
                for (QueryDocumentSnapshot document : value) {
                    Datetime datetime = document.toObject(Datetime.class);
                    list_dates.add(datetime);
                    String uid = datetime.getKey();
                    listKeysDates.add(uid);
                }
                fetchUsersByKeys(listKeysDates,list_dates);
            }
        });


    }

    private void fetchUsersByKeys(ArrayList<String> userKeys,ArrayList<Datetime> list_dates) {
        db.collection(USERS_TABLE)
                .whereIn("key", userKeys) //  "key" is the field name in the "users" collection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }
                        int i =0;
                        for (QueryDocumentSnapshot document : value) {
                            User customer = document.toObject(User.class); // Changed !! => User to Customer
                            customer.setDateTime(list_dates.get(i++));
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
                            userData.setKeyI(getCurrentUser().getUid());
                            saveUserData(userData);
                        }else {
                            authCallBack.onCreateAccountComplete(false, task.getException().getMessage().toString());
                        }
                    }
                });
    }

//    public void saveUserTimes(Datetime dateTime, User customer) {
//        // Generate a new unique document ID
//        String newDocumentId = db.collection(TIMES_TABLE).document().getId();
//
//        // Save the new queue with the unique document ID
//        db.collection(TIMES_TABLE).document(newDocumentId).set(dateTime)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            customerCallBack.onAddICustomerComplete(task);
//                        } else {
//                            customerCallBack.onAddICustomerComplete(task);
//                        }
//                    }
//                });
//    }

    public void saveUserTimes(Datetime dateTime, User customer, String serviceName) {
        String timesTable;
        if (serviceName.equals("Haircut") || serviceName.equals("Hair color") || serviceName.equals("Shaving")) {
            timesTable = TIMES_TABLE_HAIRCUT;
        } else if (serviceName.equals("Nails")) {
            timesTable = TIMES_TABLE_NAILS;
        } else if (serviceName.equals("Laser")) {
            timesTable = TIMES_TABLE_LASER;
        } else {
            // Handle unexpected service name
            return;
        }

        String newDocumentId = db.collection(timesTable).document().getId();
        db.collection(timesTable).document(newDocumentId).set(dateTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Consider handling success and failure differently
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

    public void updateUser(String uid,User user){
        this.db.collection(USERS_TABLE).document(uid).set(user)
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
                if (value != null && value.exists()) {
                    User user = value.toObject(User.class);
                    if (user != null) {
                        if(user.getImagePath() != null){
                            user.setImageUrl(downloadImageUrl(user.getImagePath()));
                        }
                        user.setKey(value.getId());
                        userCallBack.onUserFetchDataComplete(user);
                    } else {
                        // User object is null, handle appropriately
                        userCallBack.onUserFetchDataComplete(null);
                    }
                } else {
                    // Document does not exist or there was an error, handle appropriately
                    userCallBack.onUserFetchDataComplete(null);
                }
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
                    if(product.getImagePath() != null){
                        product.setImageUrl(downloadImageUrl(product.getImagePath()));
                    }
                    productList.add(product);
                }

                productCallBack.onFetchProductsComplete(productList);
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


    public Task<ArrayList<Datetime>> fetchHaircutsAppointments(String userId) {
        ArrayList<Datetime> appointmentsList = new ArrayList<>();
        CollectionReference hairCutCollection = db.collection("TSchedule_haircut");

        // Use Task to handle the asynchronous operation
        Task<QuerySnapshot> querySnapshotTask = hairCutCollection.whereEqualTo("key", userId).get();

        return querySnapshotTask.continueWith(new Continuation<QuerySnapshot, ArrayList<Datetime>>() {
            @Override
            public ArrayList<Datetime> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Retrieve data from document
                        String formattedTime = document.getString("formattedTime");
                        String key = document.getString("key");
                        String serviceName = document.getString("serviceName");
                        Timestamp timestamp = document.getTimestamp("timestamp");

                        // Create an Appointment object
                        Datetime appointment = new Datetime(serviceName, timestamp, key);
                        appointmentsList.add(appointment);
                    }

                    // Log appointments for debugging
                    for (Datetime appointment : appointmentsList) {
                        Log.d("AppointmentInfo", "FormattedTime: " + appointment.getFormattedTime() +
                                ", Key: " + appointment.getKey() +
                                ", ServiceName: " + appointment.getServiceName() +
                                ", Timestamp: " + appointment.getFormattedTime());
                    }

                    // Return the populated list
                    return appointmentsList;
                } else {
                    Log.e("FetchAppointments", "Error getting documents: ", task.getException());
                }
                return null;
            }
        });
    }


//    public ArrayList<Datetime> fetchNailsAppointments(String userId) {
//        ArrayList<Datetime> appointmentsList = new ArrayList<>();
//        CollectionReference hairCutCollection = db.collection("TSchedule_nails");
//        hairCutCollection.whereEqualTo("key", userId).get()
//                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                // Retrieve data from document
//                                String formattedTime = document.getString("formattedTime");
//                                String key = document.getString("key");
//                                String serviceName = document.getString("serviceName");
//                                Timestamp timestamp = document.getTimestamp("timestamp");
//
//                                // Create an Appointment object
//                                Datetime appointment = new Datetime(serviceName, timestamp, key);
//                                appointmentsList.add(appointment);
//                            }
//
//                            // Now, appointmentsList contains all the appointments for the user
//                            // Do whatever you want with this list (e.g., display, process, etc.)
//                            for (Datetime appointment : appointmentsList) {
//                                Log.d("AppointmentInfo", "FormattedTime: " + appointment.getFormattedTime() +
//                                        ", Key: " + appointment.getKey() +
//                                        ", ServiceName: " + appointment.getServiceName() +
//                                        ", Timestamp: " + appointment.getFormattedTime());
//                            }
//
//                        } else {
//                            Log.e("FetchAppointments", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        return appointmentsList;
//    }

    public Task<ArrayList<Datetime>> fetchNailsAppointments(String userId) {
        ArrayList<Datetime> appointmentsList = new ArrayList<>();
        CollectionReference nailsCollection = db.collection("TSchedule_nails");

        // Use Task to handle the asynchronous operation
        Task<QuerySnapshot> querySnapshotTask = nailsCollection.whereEqualTo("key", userId).get();

        return querySnapshotTask.continueWith(new Continuation<QuerySnapshot, ArrayList<Datetime>>() {
            @Override
            public ArrayList<Datetime> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Retrieve data from document
                        String formattedTime = document.getString("formattedTime");
                        String key = document.getString("key");
                        String serviceName = document.getString("serviceName");
                        Timestamp timestamp = document.getTimestamp("timestamp");

                        // Create an Appointment object
                        Datetime appointment = new Datetime(serviceName, timestamp, key);
                        appointmentsList.add(appointment);
                    }

                    // Log appointments for debugging
                    for (Datetime appointment : appointmentsList) {
                        Log.d("AppointmentInfo", "FormattedTime: " + appointment.getFormattedTime() +
                                ", Key: " + appointment.getKey() +
                                ", ServiceName: " + appointment.getServiceName() +
                                ", Timestamp: " + appointment.getFormattedTime());
                    }

                    // Return the populated list
                    return appointmentsList;
                } else {
                    Log.e("FetchAppointments", "Error getting documents: ", task.getException());
                }
                return null;
            }
        });
    }


    public Task<ArrayList<Datetime>> fetchLaserAppointments(String userId) {
        ArrayList<Datetime> appointmentsList = new ArrayList<>();
        CollectionReference laserCollection = db.collection("TSchedule_laser");

        // Use Task to handle the asynchronous operation
        Task<QuerySnapshot> querySnapshotTask = laserCollection.whereEqualTo("key", userId).get();

        return querySnapshotTask.continueWith(new Continuation<QuerySnapshot, ArrayList<Datetime>>() {
            @Override
            public ArrayList<Datetime> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Retrieve data from document
                        String formattedTime = document.getString("formattedTime");
                        String key = document.getString("key");
                        String serviceName = document.getString("serviceName");
                        Timestamp timestamp = document.getTimestamp("timestamp");

                        // Create an Appointment object
                        Datetime appointment = new Datetime(serviceName, timestamp, key);
                        appointmentsList.add(appointment);
                    }

                    // Log appointments for debugging
                    for (Datetime appointment : appointmentsList) {
                        Log.d("AppointmentInfo", "FormattedTime: " + appointment.getFormattedTime() +
                                ", Key: " + appointment.getKey() +
                                ", ServiceName: " + appointment.getServiceName() +
                                ", Timestamp: " + appointment.getFormattedTime());
                    }

                    // Return the populated list
                    return appointmentsList;
                } else {
                    Log.e("FetchAppointments", "Error getting documents: ", task.getException());
                }
                return null;
            }
        });
    }


//    public ArrayList<Datetime> fetchLaserAppointments(String userId) {
//        ArrayList<Datetime> appointmentsList = new ArrayList<>();
//        CollectionReference hairCutCollection = db.collection("TSchedule_laser");
//        hairCutCollection.whereEqualTo("key", userId).get()
//                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                // Retrieve data from document
//                                String formattedTime = document.getString("formattedTime");
//                                String key = document.getString("key");
//                                String serviceName = document.getString("serviceName");
//                                Timestamp timestamp = document.getTimestamp("timestamp");
//
//                                // Create an Appointment object
//                                Datetime appointment = new Datetime(serviceName, timestamp, key);
//                                appointmentsList.add(appointment);
//                            }
//
//                            // Now, appointmentsList contains all the appointments for the user
//                            // Do whatever you want with this list (e.g., display, process, etc.)
//                            for (Datetime appointment : appointmentsList) {
//                                Log.d("AppointmentInfo", "FormattedTime: " + appointment.getFormattedTime() +
//                                        ", Key: " + appointment.getKey() +
//                                        ", ServiceName: " + appointment.getServiceName() +
//                                        ", Timestamp: " + appointment.getFormattedDate() + " " + appointment.getFormattedTime());
//                            }
//
//                        } else {
//                            Log.e("FetchAppointments", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        return appointmentsList;
//    }
}


