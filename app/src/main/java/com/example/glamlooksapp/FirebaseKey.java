package com.example.glamlooksapp;


import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class FirebaseKey implements Serializable {
    protected String key;

    public FirebaseKey() {}
    public FirebaseKey setKey(String key) {
        this.key = key;
        return this;
    }
    @Exclude
    public String getKey() {
        return key;
    }

}