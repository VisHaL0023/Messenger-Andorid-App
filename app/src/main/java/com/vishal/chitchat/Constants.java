package com.vishal.chitchat;

import java.util.HashMap;

public class Constants {

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";

    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";

    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final  String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final  String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final  String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static HashMap<String,String> getRemoteMessageHeaders()
    {
        HashMap<String ,String> headers = new HashMap<>();
        headers.put(Constants.REMOTE_MSG_AUTHORIZATION,"key=AAAAhGNFgQs:APA91bFUh969OhuXT-MhZqWnqisAdz-8tqjoleZ4QLdOe65IJffA7YP_OV-w1lx2iTKyxxbhAx_FgvTopYGGLro9ghNOeQ6Dv62QFadg_XxNpRbrgWqEME95TJb054ciNWWyVRyKuK62");
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
