package com.example.glamlooksapp.utils;
import android.net.Uri;
import android.util.Log;

import java.security.NoSuchAlgorithmException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.glamlooksapp.callback.AuthCallBack;
import com.example.glamlooksapp.callback.CustomerCallBack;
import com.example.glamlooksapp.callback.DatetimeCallback;
import com.example.glamlooksapp.callback.ManagerAddedCallback;
import com.example.glamlooksapp.callback.ProductCallBack;
import com.example.glamlooksapp.callback.UserCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.Objects;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;


public class Database {
    public static final String USERS_TABLE = "Customers";
    public static final String TIMES_TABLE = "TSchedule";
    public static final String USERS_PROFILE_IMAGES = "Users/";
    public static final String PRODUCTS_TABLE = "Products";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private AuthCallBack authCallBack;
    private ProductCallBack productCallBack;
    private CustomerCallBack customerCallBack;
    private ManagerAddedCallback managerAddedListener;
    private UserCallBack userCallBack;
    private ArrayList<Product> productList;  // Add this list to store products
    private ArrayList<Customer> customersWantedList ;
    private ArrayList<String> datesKeysList;
    private ArrayList<Datetime> userDatesList;
    private ArrayList<Datetime> appointmentsList;
    private ArrayList<Manager> managersList;


    public Database(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        productList = new ArrayList<Product>(1000);
        customersWantedList = new ArrayList<Customer>(1000);
        appointmentsList = new ArrayList<Datetime>();
        datesKeysList = new ArrayList<>();
        userDatesList = new ArrayList<>();
        managersList = new ArrayList<>();
    }

    public void setOnManagerAddedListener(ManagerAddedCallback listener) {
        this.managerAddedListener = listener;
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

public void fetchUserDatesByService(String serviceName) {
    db.collection(TIMES_TABLE)
            .whereEqualTo("serviceName", serviceName)
            .orderBy("formattedDate")
            .orderBy("formattedTime")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "Error fetching documents: ", error);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.d(TAG, "No data found");
                        return;
                    }

                    Log.d("fetchUserDatesByService", "serviceName: " + serviceName);
                    Log.d("fetchUserDatesByService", "ValueKeys2: " + value.size());

                    datesKeysList = new ArrayList<>();
                    appointmentsList = new ArrayList<>(); // Initialize the list

                    int i = 0;
                    for (QueryDocumentSnapshot document : value) {
                        i++;
                        Datetime datetime = document.toObject(Datetime.class);
                        appointmentsList.add(datetime);
                        String uid = datetime.getKey();
                        Log.d("fetchUserDatesByService", "ValueKeys: " + i + uid);
                        datesKeysList.add(uid);
                    }

                    fetchUsersByKeys(datesKeysList, appointmentsList);
                }
            });
}

    public void fetchUsersByKeys(ArrayList<String> userKeys, ArrayList<Datetime> list_dates) {
        if (userKeys == null || userKeys.isEmpty()) {
            Log.d("FirestoreData", "No user keys provided for filtering");
            return;
        }
        db.collection(USERS_TABLE)
                .whereIn("key", userKeys)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle Firestore error
                            Log.e("FirestoreData", "Error fetching users: " + error.getMessage());
                            return;
                        }

                        if (value == null || value.isEmpty()) {
                            // Handle case when no users are found
                            Log.d("FirestoreData", "No users found");
                            return;
                        }
                        // Print the value of 'value' variable
                        Log.d("FirestoreData", "ValueKeys: " + value.size());
                        // Process retrieved users
                        int i = 0;
                        for (QueryDocumentSnapshot document : value) {
                            Customer customer = document.toObject(Customer.class);
                            customer.setDateTime(list_dates.get(i++));
                            customersWantedList.add(customer);
                        }
                        // Notify listener with retrieved users
                        customerCallBack.onFetchCustomerComplete(customersWantedList);
                    }
                });
    }

public void fetchUserDatesByKey(String uid) {
    db.collection(TIMES_TABLE)
            .whereEqualTo("key", uid)
            .orderBy("formattedDate")
            .orderBy("formattedTime")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "Error fetching documents: ", error);
                        return;
                    }

                    userDatesList = new ArrayList<>();
                    if (value != null) {
                        Log.e(TAG, "ValueKeys: " + value.size());
                        for (QueryDocumentSnapshot document : value) {
                            Datetime datetime = document.toObject(Datetime.class);
                            userDatesList.add(datetime);
                        }
                    }
                    customerCallBack.onCompleteFetchUserDates(userDatesList);
                    Log.d("fetchUserDatesByKey", "success");
                }
            });
}



    public void fetchUserDatesByKeyAndDate(String uid,String formattedDate, CustomerCallBack customerCallBack) {
        db.collection(TIMES_TABLE)
                .whereEqualTo("managerId", uid)
                .whereEqualTo("formattedDate", formattedDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Error fetching user dates by key and date: ", error);
                            return;
                        }

                        userDatesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            Datetime datetime = document.toObject(Datetime.class);
                            userDatesList.add(datetime);
                        }

                        customerCallBack.onCompleteFetchUserDates(userDatesList);
                        Log.d("fetchUserDatesByKeyAndDate", "success");
                    }
                });
    }

    public void createAccount(String email, String password, User userData) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(getCurrentUser() != null){
                            Log.d("CreateAccount", "User created");
                            userData.setKeyI(getCurrentUser().getUid());
                            saveUserData(userData);
                            Log.d("CreateAccount", "success");
                        }else {
                            Log.e("CreateAccount", "User not created");
                            authCallBack.onCreateAccountComplete(false, task.getException().getMessage().toString());
                        }
                    }
                });
    }

    public void saveCustomerAppointment(Datetime dateTime, CustomerCallBack callBack) {
        // Get a reference to the Firestore collection
        CollectionReference timesCollection = db.collection(TIMES_TABLE);
        // Add a new document with an auto-generated ID
        timesCollection.add(dateTime)
                .addOnSuccessListener(documentReference -> {
                    // Get the auto-generated ID of the new document
                    String dateTimeId = documentReference.getId();
                    // Set the auto-generated ID in the Datetime object
                    dateTime.setUUid(dateTimeId);
                    // Update the document with the generated ID
                    timesCollection.document(dateTimeId).set(dateTime)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // If successful, call the callback with the generated ID
                                    callBack.onAddICustomerComplete(task);
                                    Log.d("saveCustomerAppointment", "success");
                                } else {
                                    // If not successful, call the callback with null
                                    callBack.onAddICustomerComplete(null);
                                    Log.e("saveCustomerAppointment", "failure");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    callBack.onAddICustomerComplete(null);
                });
    }

    public void saveUserData(User user){
        this.db.collection(USERS_TABLE).document(user.getKey()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            authCallBack.onCreateAccountComplete(true, "");
                            Log.d("saveUserData", "success"); // Log success
                        }
                        else {
                            authCallBack.onCreateAccountComplete(false, Objects.requireNonNull(task.getException()).getMessage());
                            Log.e("saveUserData", "failure"); // Log failure
                        }
                    }
                });
    }

    public void updateCustomer(Customer customer){
        this.db.collection(USERS_TABLE).document(customer.getKey()).set(customer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userCallBack.onUpdateComplete(task);
                    }
                });
    }

    public void updateManager(Manager manager){
        this.db.collection(USERS_TABLE).document(manager.getKey()).set(manager)
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
                    Customer customer = value.toObject(Customer.class);
                    if (customer != null) {
                        if(customer.getImagePath() != null){
                            customer.setImageUrl(downloadImageUrl(customer.getImagePath()));
                        }
                        customer.setKey(value.getId());
                        userCallBack.onCustomerFetchDataComplete(customer);
                    } else {
                        // User object is null, handle appropriately
                        try {
                            userCallBack.onManagerFetchDataComplete(null);
                        } catch (NoSuchAlgorithmException e) {
                            Log.e("fetchUserData", "Error fetching user data: ", e);
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    // Document does not exist or there was an error, handle appropriately
                    try {
                        userCallBack.onManagerFetchDataComplete(null);
                    } catch (NoSuchAlgorithmException e) {
                        Log.e("fetchUserData", "Error fetching user data: ", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void fetchUserData(String uid,UserCallBack callBack){
        db.collection(USERS_TABLE).document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Customer customer = value.toObject(Customer.class);
                    if (customer != null) {
                        if(customer.getImagePath() != null){
                            customer.setImageUrl(downloadImageUrl(customer.getImagePath()));
                        }
                        customer.setKey(value.getId());
                        callBack.onCustomerFetchDataComplete(customer);
                    } else {
                        // User object is null, handle appropriately
                        try {
                            callBack.onManagerFetchDataComplete(null);
                        } catch (NoSuchAlgorithmException e) {
                            Log.e("fetchUserData", "Error fetching user data: ", e);
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    // Document does not exist or there was an error, handle appropriately
                    try {
                        callBack.onManagerFetchDataComplete(null);
                    } catch (NoSuchAlgorithmException e) {
                        Log.e("fetchUserData", "Error fetching user data: ", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void fetchManagerData(String uid){
        db.collection(USERS_TABLE).document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Manager manager = value.toObject(Manager.class);
                    if (manager != null) {
                        if(manager.getImagePath() != null){
                            manager.setImageUrl(downloadImageUrl(manager.getImagePath()));
                        }
                        manager.setKey(value.getId());
                        try {
                            userCallBack.onManagerFetchDataComplete(manager);
                        } catch (NoSuchAlgorithmException e) {
                            Log.e("fetchManagerData", "Error fetching manager data: ", e);
                            throw new RuntimeException(e);
                        }
                    } else {
                        // User object is null, handle appropriately
                        try {
                            userCallBack.onManagerFetchDataComplete(null);
                        } catch (NoSuchAlgorithmException e) {
                            Log.e("fetchManagerData", "Error fetching manager data: ", e);
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    // Document does not exist or there was an error, handle appropriately
                    try {
                        userCallBack.onManagerFetchDataComplete(null);
                    } catch (NoSuchAlgorithmException e) {
                        Log.e("fetchManagerData", "Error fetching manager data: ", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void fetchManagerData(String managerId, UserCallBack userCallBack) {
        // Check if the managerId is not null
        if (managerId == null) {
            // Handle the case when managerId is null
            Log.d("fetchManagerData", "Manager ID is null");
            return;
        }
        CollectionReference managersRef = db.collection(USERS_TABLE);
        // Query the manager document by managerId
        managersRef.document(managerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Convert the document snapshot to a Manager object
                    Manager manager = document.toObject(Manager.class);
                    if (manager != null) {
                        // Callback to notify that manager data has been fetched successfully
                        try {
                            userCallBack.onManagerFetchDataComplete(manager);
                        } catch (NoSuchAlgorithmException e) {
                            Log.e("fetchManagerData", "Error fetching manager data: ", e);
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Callback to handle the case when manager object is null
                        try {
                            userCallBack.onManagerFetchDataComplete(null);
                        } catch (NoSuchAlgorithmException e) {
                            Log.e("fetchManagerData", "Error fetching manager data: ", e);
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    // Callback to handle the case when document does not exist
                    try {
                        userCallBack.onManagerFetchDataComplete(null);
                    } catch (NoSuchAlgorithmException e) {
                        Log.e("fetchManagerData", "Error fetching manager data: ", e);
                        throw new RuntimeException(e);
                    }
                }
            } else {
                // Callback to handle the case when fetching manager data fails
                try {
                    userCallBack.onManagerFetchDataComplete(null);
                } catch (NoSuchAlgorithmException e) {
                    Log.e("fetchManagerData", "Error fetching manager data: ", e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void fetchManagersData() {
        db.collection(USERS_TABLE)
                .whereEqualTo("account_type", 1) // Add condition to fetch only managers with account_type = 1
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        for (DocumentSnapshot document : value) {
                            Manager manager = document.toObject(Manager.class);
                            if (manager != null && manager.getDeleted() == 0) {
                                if (manager.getImagePath() != null) {
                                    manager.setImageUrl(downloadImageUrl(manager.getImagePath()));
                                }
                                manager.setKey(document.getId());
                                managersList.add(manager);
                                managerAddedListener.onManagerFetchDataComplete(managersList);

                            }
                        }

                        // Handle case where no managers with account_type = 1 are found
                        managerAddedListener.onManagerFetchDataComplete(null);
                        Log.d("fetchManagersData", "success");
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
                Log.d("fetchProducts", "success");
                productCallBack.onFetchProductsComplete(productList);
            }
        });
    }

    public void uploadProduct(Product product){
        this.db.collection(PRODUCTS_TABLE).document().set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("uploadProduct", "success");
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
        Log.d("DownloadImageUrl", "success");
        return task.getResult().toString();
    }

    public boolean uploadImage(Uri selectedImageUri, String path) {
        UploadTask task =  this.mStorage.getReference(path).putFile(selectedImageUri);
        while(!task.isComplete());
        Log.d("UploadImage", "success");
        return task.isSuccessful();
    }

    public void deleteUserTime(String datetimeUid) {
        Log.d("DeleteUserTime", datetimeUid);

        db.collection(TIMES_TABLE)
                .document(datetimeUid)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DeleteUserTime", "success");
                    } else {
                        Log.w("DeleteUserTime", "Error deleting document", task.getException());
                    }
                });
    }

    public void deleteUserTime(String datetimeUid, UserCallBack callBack) {
        db.collection(TIMES_TABLE)
                .document(datetimeUid)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("deleteUserTime", "success");
                        // Call the callback method to notify completion
                        callBack.onDeleteComplete(task);
                    } else {
                        Log.w("deleteUserTime", "Error deleting document", task.getException());
                    }
                });
    }

    public void fetchDatesByManagerId(String managerId, DatetimeCallback callback) {
        // Check if managerId is not null
        if (managerId == null) {
            // Handle the case when managerId is null
            return;
        }
        // Get a reference to the Firestore collection
        CollectionReference datesCollection = db.collection(TIMES_TABLE);
        // Query the collection for dates with matching managerId
        datesCollection.whereEqualTo("managerId", managerId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle Firestore error
                        Log.e("fetchDatesByManagerId", "Error fetching dates by managerId: ", error);
                        return;
                    }
                    // Create a list to store retrieved dates
                    ArrayList<Datetime> datesList = new ArrayList<>();
                    // Check if the query result is not empty
                    if (value != null && !value.isEmpty()) {
                        // Loop through each document in the query result
                        for (QueryDocumentSnapshot document : value) {
                            // Convert Firestore document to Datetime object
                            Datetime datetime = document.toObject(Datetime.class);
                            // Add the datetime object to the list
                            datesList.add(datetime);
                        }
                    }
                    // Call the callback method with the retrieved dates
                    Log.d("fetchDatesByManagerId", "success");
                    callback.onDatetimeFetchComplete(datesList);
                });
    }

    public void removeUser(String key) {
        // Delete user from Firebase Authentication
        Objects.requireNonNull(mAuth.getCurrentUser()).delete();

        // Delete user document from Firestore
        db.collection(USERS_TABLE)
                .document(key)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("User deleted successfully", "success");
                    } else {
                        Log.w("not success to delete user", "Error deleting document", task.getException());
                    }
                });
    }

    public void getProductsByName(String productName, ProductCallBack callback) {
        db.collection("products").whereEqualTo("name", productName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Product> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    if(product.getImagePath() != null) {
                        product.setImageUrl(downloadImageUrl(product.getImagePath()));
                    }
                    productList.add(product);
                }
                callback.onFetchProductsComplete(productList);
            } else {
                Log.d("Database", "Error getting documents: ", task.getException());
            }
        });
    }




}