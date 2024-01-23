package com.example.glamlooksapp.utils;


import com.google.firebase.firestore.Exclude;

public class Product extends FirebaseKey{
    private String name;
    private String description;
    private String uid;
    private String imagePath;
    private String imageUrl;

    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Product() {}

    public Product(String name, String description,String uid,
                   String imagePath,String imageUrl,double price) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.imagePath = imagePath;
        this.imageUrl = imageUrl;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Product setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }
    @Exclude
    public String getImageUrl() {
        return imageUrl;
    }

    public Product setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }







}
