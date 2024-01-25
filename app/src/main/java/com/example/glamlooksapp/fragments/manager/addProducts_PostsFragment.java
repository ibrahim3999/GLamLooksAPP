package com.example.glamlooksapp.fragments.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.home.ManagerActivity;
import com.example.glamlooksapp.home.UpdateProfileActivity;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Generic;
import com.example.glamlooksapp.utils.Product;
import com.example.glamlooksapp.callback.ProductCallBack;
import com.example.glamlooksapp.utils.ProductManager;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class addProducts_PostsFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private AppCompatActivity activity;

    private EditText editTextProductName, editTextProductPrice;
    private Button btnUploadPhoto, btnAddProduct;
    private ImageView imageViewSelectedPhoto;

    private Database database;
    private Uri selectedImageUri;

    public addProducts_PostsFragment(AppCompatActivity activity) {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_products__posts, container, false);

       findViews(view);

        initVars();




        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        } else {
            // Handle the case where the hosting activity is not an instance of AppCompatActivity
            throw new IllegalArgumentException("Hosting activity must be AppCompatActivity");
        }
    }




    private void initVars(){
       database = new Database();

        database.setProductCallBack(new ProductCallBack() {
            @Override
            public void onAddIProductsComplete(Task<Void> task) {

                if(task.isSuccessful()) {
                    showToast("Added!");
                }
            }

            @Override
            public void onFetchProductsComplete(ArrayList<Product> product) {

            }
        });

       btnAddProduct.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!checkInputs()) return;
               addProduct();
           }
       });



       btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(Generic.checkPermissions(activity)){ // Here the issue with the activity is null !!
                   showImageSourceDialog();
               }else{
                   Toast.makeText(activity, "no permissions to access camera", Toast.LENGTH_SHORT).show();
               }
           }
       });

    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private boolean checkInputs() {
        String productName = editTextProductName.getText().toString();
        String productPrice = editTextProductPrice.getText().toString();

        if(productName.isEmpty()){
            showToast("Name is required!");
            return false;
        }
        if(productPrice.isEmpty()){
            showToast("Price is required!");
            return false;
        }
        if(selectedImageUri == null){
            Toast.makeText(activity, "image is required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    private void addProduct() {
        // Implement add product logic here
        String productName = editTextProductName.getText().toString();
        String productPrice = editTextProductPrice.getText().toString();
        Random random = new Random();
        int randomNumber = 1_000_000 + random.nextInt(9_999_999);
        String ext = Generic.getFileExtension(activity, selectedImageUri);
        String imagePath = "Products/"  + "_" + randomNumber + "." + ext;
        if (database.uploadImage(selectedImageUri, imagePath)) {


            Product product = new Product();
            product.setPrice(Double.parseDouble(productPrice));
            product.setName(productName);

            database.uploadProduct(product);
        }
    }









    private void showImageSourceDialog() {
        CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                                selectedImageUri = intent.getData();
                                final InputStream imageStream = activity.getContentResolver().openInputStream(selectedImageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                imageViewSelectedPhoto.setImageBitmap(selectedImage);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(activity, "Gallery canceled", Toast.LENGTH_SHORT).show();
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
                            imageViewSelectedPhoto.setImageBitmap(bitmap);
                            selectedImageUri = Generic.getImageUri(activity, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(activity, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });




    private void findViews(View view) {

        // Initialize views
        editTextProductName = view.findViewById(R.id.editTextProductName);
        editTextProductPrice = view.findViewById(R.id.editTextProductPrice);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        imageViewSelectedPhoto = view.findViewById(R.id.imageViewSelectedPhoto);
//        activity = new AppCompatActivity();
    }
}
