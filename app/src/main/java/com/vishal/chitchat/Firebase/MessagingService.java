package com.vishal.chitchat.Firebase;


import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vishal.chitchat.Activities.IncommingInvitationActivity;
import com.vishal.chitchat.Constants;

import org.jetbrains.annotations.NotNull;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);


    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //getting data from RemoteMessage
        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);

        if(type != null)
        {
            if(type.equals(Constants.REMOTE_MSG_INVITATION))
            {
                Intent intent = new Intent(getApplicationContext(), IncommingInvitationActivity.class);
                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE,remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
                intent.putExtra("username",remoteMessage.getData().get("username"));
                intent.putExtra("phoneNo",remoteMessage.getData().get("phoneNo"));
                intent.putExtra("profile",remoteMessage.getData().get("profile"));

                //used when call accpeted
                intent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN,remoteMessage.getData()
                .get(Constants.REMOTE_MSG_INVITER_TOKEN));

                //for meeting Room of JITSI
                intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM));


                //since we are starting Activity from non activity WE nee to Set A flag
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE))
            {
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }
}

