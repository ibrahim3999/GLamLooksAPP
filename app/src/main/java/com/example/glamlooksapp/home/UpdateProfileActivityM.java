package com.example.glamlooksapp.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.fragments.manager.ProfileMFragment;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Generic;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Service;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivityM extends AppCompatActivity {
    private Uri selectedImageUri;
    private Database database;
    private CircleImageView image;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout phone;
    private ProgressBar editAccount_PB_loading;
    private Button updateAccountBtn;
    private Manager currentManager;
    private FloatingActionButton uploadImageBtn;
    private TextInputLayout serviceName;
    private TextInputLayout serviceDuration;
    private TextInputLayout servicePrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_m);
        Intent intent = getIntent();
        Serializable serializable = intent.getSerializableExtra(ProfileMFragment.USER_INTENT_KEY);
        if (serializable instanceof Manager) {
            currentManager = (Manager) serializable;
        }
        findViews();
        initVars();
        displayUser(currentManager);
    }

    private void displayUser(Manager manager) {
        Objects.requireNonNull(firstName.getEditText()).setText(manager.getFirstname());
        Objects.requireNonNull(lastName.getEditText()).setText(manager.getLastname());
        Objects.requireNonNull(phone.getEditText()).setText(manager.getPhoneNumber());
        Objects.requireNonNull(serviceName.getEditText()).setText(manager.getService().getServiceName());
        Objects.requireNonNull(serviceDuration.getEditText()).setText(manager.getService().getDuration());
        Objects.requireNonNull(servicePrice.getEditText()).setText(Double.toString(manager.getService().getPrice()));
        if(manager.getImageUrl() != null){
            // set image profile
            Glide
                    .with(this)
                    .load(manager.getImageUrl())
                    .centerCrop()
                    .into(image);
        }
    }


    private void findViews() {
        image = findViewById(R.id.editAccount_IV_image);
        firstName = findViewById(R.id.editAccount_TF_firstName);
        lastName = findViewById(R.id.editAccount_TF_lastName);
        editAccount_PB_loading = findViewById(R.id.editAccount_PB_loading);
        updateAccountBtn = findViewById(R.id.editAccount_BTN_updateAccount);
        uploadImageBtn = findViewById(R.id.editAccount_FBTN_uploadImage);
        phone = findViewById(R.id.editAccount_TF_phone);
        serviceName = findViewById(R.id.editAccount_service_name);
        serviceDuration = findViewById(R.id.edit_account_service_duration);
        servicePrice = findViewById(R.id.edit_account_service_price);
    }

    private void initVars() {
        database = new Database();
        database.setUserCallBack(new UserCallBack() {
            @Override
            public void onManagerFetchDataComplete(Manager manager) {}
            @Override
            public void onCustomerFetchDataComplete(Customer customer) {}
            @Override
            public void onUpdateComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("UpdateProfileActivityM", "onUpdateComplete: success");
                    Toast.makeText(UpdateProfileActivityM.this, "Profile update success",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Log.d("UpdateProfileActivityM", "onUpdateComplete: failed");
                    Toast.makeText(UpdateProfileActivityM.this, task.getException().getMessage().toString() ,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDeleteComplete(Task<Void> task) {}
        });

        updateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Manager manager = new Manager();
                manager.setLastname(Objects.requireNonNull(lastName.getEditText()).getText().toString());
                manager.setFirstname(Objects.requireNonNull(firstName.getEditText()).getText().toString());
                manager.setPhoneNumber(Objects.requireNonNull(phone.getEditText()).getText().toString());
                String serviceName = Objects.requireNonNull(UpdateProfileActivityM.this.serviceName.getEditText()).getText().toString();
                double price = Double.valueOf(Objects.requireNonNull(servicePrice.getEditText()).getText().toString());
                String duration = Objects.requireNonNull(serviceDuration.getEditText()).getText().toString();

                Service managerService = new Service(serviceName, price, duration);
                manager.setService(managerService);

                manager.setEmail(currentManager.getEmail());
                manager.setAccount_type(currentManager.getAccount_type());
                String uid = database.getCurrentUser().getUid();
                manager.setKey(uid);
                if(selectedImageUri != null){
                    // save image
                    String ext = Generic.getFileExtension(UpdateProfileActivityM.this, selectedImageUri);
                    String path = Database.USERS_PROFILE_IMAGES + uid + "." + ext;
                    database.uploadImage(selectedImageUri, path);
                    manager.setImagePath(path);
                    Log.d("UpdateProfileActivityM", "onClick: image path");
                }
                Log.d("UpdateProfileActivityM", "onClick: " + manager.toString());
                database.updateManager(manager);
            }
        });
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Generic.checkPermissions(UpdateProfileActivityM.this)){
                    showImageSourceDialog();
                }else{
                    Toast.makeText(UpdateProfileActivityM.this, "no permissions to access camera", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showImageSourceDialog() {
        CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivityM.this);
        builder.setTitle("Choose Image Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResults.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_results.launch(intent);
    }

    private final ActivityResultLauncher<Intent> gallery_results = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            try {
                                Intent intent = result.getData();
                                assert intent != null;
                                selectedImageUri = intent.getData();
                                final InputStream imageStream = UpdateProfileActivityM.this.getContentResolver().openInputStream(selectedImageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                image.setImageBitmap(selectedImage);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Log.e("UpdateProfileActivityM", "onActivityResult: " + e.getMessage());
                                Toast.makeText(UpdateProfileActivityM.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Log.d("UpdateProfileActivityM", "onActivityResult: canceled");
                            Toast.makeText(UpdateProfileActivityM.this, "Gallery canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            Intent intent = result.getData();
                            Bitmap bitmap = (Bitmap)  intent.getExtras().get("data");
                            image.setImageBitmap(bitmap);
                            selectedImageUri = Generic.getImageUri(UpdateProfileActivityM.this, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Log.d("UpdateProfileActivityM", "onActivityResult: canceled");
                            Toast.makeText(UpdateProfileActivityM.this, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
}