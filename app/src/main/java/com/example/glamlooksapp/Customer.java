package com.example.glamlooksapp;


import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Customer extends FirebaseKey implements Serializable {

    private String firstname;
    private String email;
    private String lastname;
    private String password;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Customer(String firstname, String email, String lastname, String password,String phoneNumber) {
        this.firstname = firstname;
        this.email = email;
        this.lastname = lastname;
        this.password = password;
        this.phoneNumber =phoneNumber;
    }

    public Customer(){}

    @Exclude
    public boolean isValid(){
        if(this.firstname == null || this.firstname.isEmpty()) return false;
        if(this.lastname == null || this.lastname.isEmpty()) return false;
        if(this.email == null || this.email.isEmpty()) return false;
        if(this.password == null || this.password.isEmpty()) return false;
        return this.phoneNumber != null && !this.phoneNumber.isEmpty();
    }


}