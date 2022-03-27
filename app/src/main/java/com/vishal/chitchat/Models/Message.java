package com.vishal.chitchat.Models;

public class Message {

    private String messageId;
    private String message;
    private String senderId;
    private String imageUrl;
    private long timeStamp;
    private int feeling = -1;
    private String receiverId;
    boolean isSeen = false;

    public Message(String message, String senderId, long timeStamp) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }


    public Message(String message, String senderId, long timeStamp,String receiverId,boolean isSeen) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.receiverId = receiverId;
        this.isSeen = isSeen;
    }

    //for Firebase
    public Message() {
    }

    //Getters
    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    //setters
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
