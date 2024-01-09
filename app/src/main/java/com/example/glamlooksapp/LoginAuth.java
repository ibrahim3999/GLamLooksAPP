package com.example.glamlooksapp;

import android.widget.EditText;
import android.widget.Toast;

public class LoginAuth {



    static Boolean userLogin(EditText emailText, EditText passwordText) {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(!email.matches(emailPattern)){
            Toast.makeText(emailText.getContext(), "Illegal  email address ",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
