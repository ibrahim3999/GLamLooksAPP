package com.example.glamlooksapp.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Datetime {

    private String serviceName;

    private Timestamp timestamp;

    private String key;


    public Datetime() {
    }

    public Datetime(String serviceName, Timestamp timestamp, String key) {
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.key = key;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return

                 serviceName + " " + timestamp.toDate().toString()

                ;
    }
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date dateTime = timestamp.toDate();
        return sdf.format(dateTime);
    }

    public String getFormattedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateTime = timestamp.toDate();
        return sdf.format(dateTime);
    }

}
