package com.example.glamlooksapp.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Datetime {

    private String serviceName;

    private Timestamp timestamp;

    private String key;

    private String uid;


    public Datetime() {
    }

    public Datetime(String serviceName, Timestamp timestamp, String key,String uid) {
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.key = key;
        this.uid = uid;
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

    public void setUUid(String uid) {
        this.uid = uid;
    }

    public String getUUid() {
        return this.uid;
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
