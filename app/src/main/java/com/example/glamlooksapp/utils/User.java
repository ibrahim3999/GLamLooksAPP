package com.example.glamlooksapp.utils;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User extends FirebaseKey implements Serializable {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phoneNumber;
    private int account_type;
    private String imagePath;
    private String imageUrl;
    private int deleted; // 0 for not deleted, 1 for deleted

    public User(User user) {
        this.setKey(user.getKey());
        this.setImagePath(user.getImagePath());
        this.setEmail(user.getEmail());
        this.setFirstname(user.getFirstname());
        this.setLastname(user.getLastname());
        this.setAccount_type(user.getAccount_type());
        this.setPassword(user.getPassword());
        this.setPhoneNumber(user.getPhoneNumber());
        this.setImageUrl(user.getImageUrl());
        this.setDeleted(user.getDeleted());
    }

    public User(String key, String firstname, String email, String lastname, String password, String phoneNumber,
                int accountType, String imagePath, String imageUrl) {
        super(key);
        this.firstname = firstname;
        this.email = email;
        this.lastname = lastname;
        this.password = password;
        this.phoneNumber =phoneNumber;
        this.account_type = accountType;
        this.imagePath = imagePath;
        this.imageUrl = imageUrl;
        this.setDeleted(0);
    }

    public User(){
        super();
        this.setDeleted(0);
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getDeleted() {
        return deleted;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    @Exclude
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

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
    @Exclude
    public String getPassword() {
        return password;
    }

    public  int getAccount_type(){return account_type;}

    public void setPassword(String password) {
        this.password = password;
    }

    public  void setAccount_type(int account_type){this.account_type = account_type;}
    public String getKey(){return key;}

    public void setKeyI(String key){this.key= key;}

}
