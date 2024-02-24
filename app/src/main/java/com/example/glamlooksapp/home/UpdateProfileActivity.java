package com.example.glamlooksapp.home;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.fragments.manager.ProfileFragment;
import com.example.glamlooksapp.utils.Customer;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.Generic;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import com.example.glamlooksapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    private Uri selectedImageUri;
    private Database db;
    private CircleImageView editAccount_IV_image;
    private TextInputLayout editAccount_TF_firstName;
    private TextInputLayout editAccount_TF_lastName;
    private TextInputLayout editAccount_TF_phone;
    private ProgressBar editAccount_PB_loading;
    private Button editAccount_BTN_updateAccount;
    private Customer currentCustomer;
    private FloatingActionButton editAccount_FBTN_uploadImage;
    private TextInputLayout editAccount_service_name;
    private TextInputLayout editAccount_service_duration;
    private TextInputLayout editAccount_service_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Intent intent = getIntent();
        currentCustomer = (Customer) intent.getSerializableExtra(ProfileFragment.USER_INTENT_KEY);
        if(!Generic.checkPermissions(this)) {
            Generic.requestPermissions(this);
        }
        findViews();
        initVars();
        displayUser(currentCustomer);
    }

    private void displayUser(Customer customer) {
        editAccount_TF_firstName.getEditText().setText(customer.getFirstname());
        editAccount_TF_lastName.getEditText().setText(customer.getLastname());
        editAccount_TF_phone.getEditText().setText(customer.getPhoneNumber());

        if(customer.getImageUrl() != null){
            // set image profile
            Glide
                    .with(this)
                    .load(customer.getImageUrl())
                    .centerCrop()
                    .into(editAccount_IV_image);
        }
    }


    private void findViews() {
        editAccount_IV_image = findViewById(R.id.editAccount_IV_image);
        editAccount_TF_firstName = findViewById(R.id.editAccount_TF_firstName);
        editAccount_TF_lastName = findViewById(R.id.editAccount_TF_lastName);
        editAccount_PB_loading = findViewById(R.id.editAccount_PB_loading);
        editAccount_BTN_updateAccount = findViewById(R.id.editAccount_BTN_updateAccount);
        editAccount_FBTN_uploadImage = findViewById(R.id.editAccount_FBTN_uploadImage);
        editAccount_TF_phone = findViewById(R.id.editAccount_TF_phone);
    }


    private void initVars() {
        db = new Database();
        db.setUserCallBack(new UserCallBack() {


            @Override
            public void onManagerFetchDataComplete(Manager manager) {}

            @Override
            public void onCustomerFetchDataComplete(Customer customer) {

            }

            @Override
            public void onUpdateComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateProfileActivity.this, "Profile update success",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(UpdateProfileActivity.this, task.getException().getMessage().toString() ,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onDeleteComplete(Task<Void> task) {

            }
        });

        editAccount_BTN_updateAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Customer customer = new Customer();


                customer.setLastname(Objects.requireNonNull(editAccount_TF_lastName.getEditText()).getText().toString());
                customer.setFirstname(Objects.requireNonNull(editAccount_TF_firstName.getEditText()).getText().toString());
                customer.setPhoneNumber(Objects.requireNonNull(editAccount_TF_phone.getEditText()).getText().toString());
                customer.setEmail(currentCustomer.getEmail());
                customer.setAccount_type(currentCustomer.getAccount_type());
                String uid = db.getCurrentUser().getUid();
                customer.setKey(uid);

                if(selectedImageUri != null){
                    // save image
                    String ext = Generic.getFileExtension(UpdateProfileActivity.this, selectedImageUri);
                    String path = Database.USERS_PROFILE_IMAGES + uid + "." + ext;
                    db.uploadImage(selectedImageUri, path);
//                    if(!db.uploadImage(selectedImageUri, path)){
//                        return;
//                    }
                    customer.setImagePath(path);
                }

                db.updateUser(customer);
            }
        });

        editAccount_FBTN_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Generic.checkPermissions(UpdateProfileActivity.this)){
                    showImageSourceDialog();
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "no permissions to access camera", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showImageSourceDialog() {
        CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
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
                                final InputStream imageStream = UpdateProfileActivity.this.getContentResolver().openInputStream(selectedImageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                editAccount_IV_image.setImageBitmap(selectedImage);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(UpdateProfileActivity.this, "Gallery canceled", Toast.LENGTH_SHORT).show();
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
                            editAccount_IV_image.setImageBitmap(bitmap);
                            selectedImageUri = Generic.getImageUri(UpdateProfileActivity.this, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(UpdateProfileActivity.this, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
}