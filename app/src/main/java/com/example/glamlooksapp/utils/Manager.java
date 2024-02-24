package com.example.glamlooksapp.utils;

import java.security.NoSuchAlgorithmException;

public class Manager extends User {

    private Service service;

    public Manager(String key, String firstname, String email, String lastname, String password,
                   String phoneNumber, int account_type, String imagePath, String imageUrl, Service service) {
        super(key, firstname, email, lastname, password, phoneNumber, account_type, imagePath, imageUrl);
        this.service = service != null ? service : new Service();
    }


    public Manager(Manager manager){
        super(manager);
        this.setService(manager.getService());
    }

    // Empty constructor
    public Manager() {
        super();
        this.service = new Service();
    }

    // Getters and setters for the new fields


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        if (service != null) {
            if (this.service == null) {
                this.service = new Service();
            }
            this.service.setServiceName(service.getServiceName());
            this.service.setDuration(service.getDuration());
            this.service.setPrice(service.getPrice());
        }
    }

}
