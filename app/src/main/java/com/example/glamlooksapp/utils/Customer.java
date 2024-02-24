package com.example.glamlooksapp.utils;

import com.google.firebase.database.Exclude;

public class Customer extends User{

    private Datetime datetime;

    public Customer(Customer customer) {
        super(customer);
        this.setDateTime(customer.getDateTime());
    }

    public Customer(String key, String firstname, String email, String lastname, String password, String phoneNumber,
                    int accountType, String imagePath, String imageUrl, Datetime datetime) {
        super(key, firstname, email, lastname, password, phoneNumber, accountType, imagePath, imageUrl);
        this.datetime =datetime;
        this.setDeleted(0);
    }


    public Customer(){
        super();
        this.datetime = new Datetime();
    }

    @Exclude
    public boolean isValid(){
        if(this.getFirstname() == null || this.getFirstname().isEmpty()) return false;
        if(this.getLastname() == null || this.getLastname().isEmpty()) return false;
        if(this.getEmail() == null || this.getEmail().isEmpty()) return false;
        if(this.getPassword() == null || this.getPassword().isEmpty()) return false;
        if(this.getDeleted() == 1) return false;
        return this.getPhoneNumber() != null && !this.getPhoneNumber().isEmpty();
    }


    public Datetime getDateTime() {
        return this.datetime;
    }

    public void setDateTime(Datetime datetime) {
        this.datetime = datetime;
    }



}
