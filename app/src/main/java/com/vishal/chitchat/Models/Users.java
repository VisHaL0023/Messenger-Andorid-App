package com.vishal.chitchat.Models;

import java.io.Serializable;

public class Users implements Serializable {
    private String uid;
    private String name;
    private String phoneNumber;
    private String profileImage;
    private String FCM;
    private String emailId;
    private String password;
    private long timestamp;
    private String statusText;
    private long lastCallTime;
    private int folllowerCount;
    private Boolean fingerPrint = false;
    private String coverPhoto;

    public Boolean getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(Boolean fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public long getLastCallTime() {
        return lastCallTime;
    }

    public void setLastCallTime(long lastCallTime) {
        this.lastCallTime = lastCallTime;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Users(){}

    public Users(String emailId, String password) {
        this.emailId = emailId;
        this.password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Users(String uid, String name1, String phone, String imageUrl, String emailId, String password, String statusText) {
        this.uid = uid;
        this.name = name1;
        this.phoneNumber = phone;
        this.profileImage = imageUrl;
        this.emailId = emailId;
        this.password = password;
        this.statusText = statusText;
    }

    public int getFolllowerCount() {
        return folllowerCount;
    }

    public void setFolllowerCount(int folllowerCount) {
        this.folllowerCount = folllowerCount;
    }

    //Empty Constructor is Necessary while dealing with Firebase
    //Firebase cannot figure out on its own what your constructor does, so that's why you need an empty constructor:
    //to allow Firebase to create a new instance of the object, which it then proceeds to fill in using reflection.


    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFCM() {
        return FCM;
    }

    public void setFCM(String FCM) {
        this.FCM = FCM;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }
}
