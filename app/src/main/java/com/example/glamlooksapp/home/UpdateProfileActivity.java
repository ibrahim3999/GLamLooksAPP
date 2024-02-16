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
import com.example.glamlooksapp.Adapter.CustomersAdapter;
import com.example.glamlooksapp.callback.UserCallBack;
import com.example.glamlooksapp.fragments.manager.ProfileFragment;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Manager;
import com.example.glamlooksapp.utils.User;
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
    private User currentUser;
    private FloatingActionButton editAccount_FBTN_uploadImage;
    private TextInputLayout editAccount_service_name;
    private TextInputLayout editAccount_service_duration;
    private TextInputLayout editAccount_service_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(ProfileFragment.USER_INTENT_KEY);
        if(!Generic.checkPermissions(this)) {
            Generic.requestPermissions(this);
        }
        findViews();
        initVars();
        displayUser(currentUser);
    }

    private void displayUser(User user) {
        editAccount_TF_firstName.getEditText().setText(user.getFirstname());
        editAccount_TF_lastName.getEditText().setText(user.getLastname());
        editAccount_TF_phone.getEditText().setText(user.getPhoneNumber());

        if(user.getImageUrl() != null){
            // set image profile
            Glide
                    .with(this)
                    .load(user.getImageUrl())
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
            public void onUserFetchDataComplete(Manager manager) {}

            @Override
            public void onUserFetchDataComplete(User user) {

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
                User user = new User();


                user.setLastname(Objects.requireNonNull(editAccount_TF_lastName.getEditText()).getText().toString());
                user.setFirstname(Objects.requireNonNull(editAccount_TF_firstName.getEditText()).getText().toString());
                user.setPhoneNumber(Objects.requireNonNull(editAccount_TF_phone.getEditText()).getText().toString());
                user.setEmail(currentUser.getEmail());
                user.setAccount_type(currentUser.getAccount_type());
                String uid = db.getCurrentUser().getUid();
                user.setKey(uid);

                if(selectedImageUri != null){
                    // save image
                    String ext = Generic.getFileExtension(UpdateProfileActivity.this, selectedImageUri);
                    String path = Database.USERS_PROFILE_IMAGES + uid + "." + ext;
                    db.uploadImage(selectedImageUri, path);
//                    if(!db.uploadImage(selectedImageUri, path)){
//                        return;
//                    }
                    user.setImagePath(path);
                }

                db.updateUser(user);
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