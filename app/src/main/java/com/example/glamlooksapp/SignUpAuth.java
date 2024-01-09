package com.example.glamlooksapp;

import android.widget.EditText;
import android.widget.Toast;

public class SignUpAuth {

    public static boolean validatePhoneNumber(EditText phoneNumber) {
        String phoneNumberStr = phoneNumber.getText().toString().trim();

        // Check if the phone number is empty
        if (phoneNumberStr.isEmpty()) {
            phoneNumber.setError("Phone number cannot be empty");
            Toast.makeText(phoneNumber.getContext(),phoneNumber.getError().toString(),Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the phone number has a valid format
        String phoneRegex = "^05[0-9]{8}$";  // Example: Accepts 05 followed by 8 digits
        if (!phoneNumberStr.matches(phoneRegex)) {
            phoneNumber.setError("Invalid phone number format");
            Toast.makeText(phoneNumber.getContext(),phoneNumber.getError().toString(),Toast.LENGTH_SHORT).show();

            return false;
        }

        // Clear the error if the phone number is valid
//        phoneNumber.setError("Done");
//        Toast.makeText(phoneNumber.getContext(),phoneNumber.getError().toString(),Toast.LENGTH_SHORT).show();
        return true;
    }

    public static Boolean validateEmail(EditText loginEmail) {
        String email = loginEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-z]+\\.com";

        if (email.isEmpty()) {
            loginEmail.setError("Username cannot be empty");
            Toast.makeText(loginEmail.getContext(),loginEmail.getError().toString(),Toast.LENGTH_SHORT).show();

            return false;
        }else if(!email.matches(emailPattern)){
            loginEmail.setError("Not in correct pattern");
            Toast.makeText(loginEmail.getContext(),loginEmail.getError().toString(),Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
//            loginEmail.setError("Done");
//            Toast.makeText(loginEmail.getContext(),loginEmail.getError().toString(),Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static Boolean validatePassword(EditText loginPassword){
        String password = loginPassword.getText().toString().trim();
        if (password.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            Toast.makeText(loginPassword.getContext(),loginPassword.getError().toString(),Toast.LENGTH_SHORT).show();
            return false;
        } else {
//            loginPassword.setError("Done");
//            Toast.makeText(loginPassword.getContext(),loginPassword.getError().toString(),Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static Boolean globalValidate(EditText phoneNumber,
                                         EditText loginPassword,EditText loginEmail
                                        ,EditText firstname,EditText lastname){

        return validatePhoneNumber(phoneNumber) &&
                validateEmail(loginEmail) && validatePassword(loginPassword)
                &&isValidName(firstname,lastname);
    }

    public static boolean isValidName(EditText firstname,EditText lastname) {
        String fname = firstname.getText().toString().trim();
        String lname = lastname.getText().toString().trim();

        String regex = "^[a-zA-Z]+$";
        Boolean res1 = fname.matches(regex);
        Boolean res2 = lname.matches(regex);

        if(!res1){
            firstname.setError("Illegal FirstName!");
            Toast.makeText(firstname.getContext(),firstname.getError().toString(),Toast.LENGTH_SHORT).show();
        }
        if(!res2){
            lastname.setError("Illegal LastName!");
            Toast.makeText(lastname.getContext(),lastname.getError().toString(),Toast.LENGTH_SHORT).show();
        }

        return res1&&res2;
    }


    public static void checkUser(EditText loginEmail, EditText loginPassword){
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        /* This function is to
         check if the user in the database or not!"*/
    }

    // Add more shared functions here
}
