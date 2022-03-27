package com.vishal.chitchat.Network;


import com.vishal.chitchat.Models.Users;

public interface
UsersListener {
    void initiateVideoMeeting(Users user);

    void initiateAudioMeeting(Users user);
}
