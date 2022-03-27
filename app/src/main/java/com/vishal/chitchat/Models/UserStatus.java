package com.vishal.chitchat.Models;

import java.util.ArrayList;

public class UserStatus {
    private String name, profileImage;
    private long lastUpdated;
    private long statusTime;
    private ArrayList<Status> statuses;



    public UserStatus(String name, String profileImage, long lastUpdated, ArrayList<Status> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public UserStatus() {

    }

    public long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(long statusTime) {
        this.statusTime = statusTime;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
