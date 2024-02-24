package com.example.glamlooksapp.utils;

import java.security.NoSuchAlgorithmException;

public class Manager extends User {
    private Service service;
    public Manager(String key, String firstname, String email, String lastname, String password,
                   String phoneNumber, int account_type, String imagePath, String imageUrl, Service service)
            throws NoSuchAlgorithmException {
        super(key, firstname, email, lastname, password, phoneNumber, account_type, imagePath, imageUrl);
        this.setService(service);
    }

    public Manager(Manager manager){
        super(manager);
        this.setService(manager.getService());
    }

    // Empty constructor
    public Manager() {
        super();
    }

    // Getters and setters for the new fields
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        if (service != null) {
            this.service = new Service(service);
        }
        else {
            this.service = new Service();
        }
    }

}
