package com.example.glamlooksapp.utils;

import java.io.Serializable;

public class Service extends FirebaseKey implements Serializable {


    private String serviceName;
    private Double Price;
    private String duration;


    public Service(String key, String serviceName, Double price, String duration) {
        super(key);
        this.serviceName = serviceName;
        Price = price;
        this.duration = duration;
    }

    public Service(String serviceName, Double price, String duration) {
        this.serviceName = serviceName;
        Price = price;
        this.duration = duration;
    }

    public Service(){}


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    @Override
    public String toString() {
        return  serviceName + " " +
                 Price +" " +
                 duration
                ;
    }


}
