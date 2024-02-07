package com.example.glamlooksapp.utils;

import com.google.type.DateTime;

import java.security.NoSuchAlgorithmException;

public class Manager extends User {

    private Service service;

    public Manager(String key, String firstname, String email, String lastname, String password,
                   String phoneNumber, int account_type, String imagePath, String imageUrl,
                   Datetime datetime, Service service) {
        super(key, firstname, email, lastname, password, phoneNumber, account_type, imagePath, imageUrl, datetime);
        this.service = service;
    }


    public Manager(String key, String firstname, String email, String lastname, String password,
                   String phoneNumber, int account_type, String imagePath, String imageUrl,
                     Service service) throws NoSuchAlgorithmException {
        this.setAccount_type(account_type);
        this.setKeyI(key);
        this.setEmail(email);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhoneNumber(phoneNumber);
        this.setImagePath(imagePath);
        this.setImageUrl(imageUrl);
        // Generate a random salt
        byte[] salt = PasswordUtils.generateSalt();
        this.setPassword(PasswordUtils.hashPassword(password,salt));
        this.service = service;
    }

    // Empty constructor
    public Manager() {}

    // Getters and setters for the new fields


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
