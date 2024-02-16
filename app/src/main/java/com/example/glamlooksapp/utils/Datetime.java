package com.example.glamlooksapp.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Datetime {

    private String serviceName;

    private Timestamp timestamp;

    private String key;

    private String uid;
    private String managerId;
    private int deleted;

    public Datetime() {
    }

    public Datetime(String serviceName, Timestamp timestamp, String key,String uid,String managerId) {
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.key = key;
        this.uid = uid;
        this.managerId = managerId;
        this.deleted = 0;
    }

    public Datetime(Datetime datetime) {
        this.serviceName = datetime.getServiceName();
        this.timestamp = datetime.getTimestamp();
        this.key = datetime.getKey();
        this.uid = datetime.getUUid();
        this.managerId = datetime.getManagerId();
        this.deleted = datetime.getDeleted();
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
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

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getUUid() {
        return this.uid;
    }


    @Override
    public String toString() {
        return

                 serviceName + " " ;


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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Datetime)) {
            return false;
        }
        Datetime other = (Datetime) obj;
        return Objects.equals(managerId, other.managerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerId);
    }

}
