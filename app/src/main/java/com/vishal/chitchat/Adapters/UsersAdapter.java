package com.vishal.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Activities.chatActivity;
import com.vishal.chitchat.Activities.imageViewer;
import com.vishal.chitchat.Models.AES;
import com.vishal.chitchat.Models.Message;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.Services.TimeAgo;
import com.vishal.chitchat.databinding.RowConversationBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    Context context;
    ArrayList<Users> users;
    int dpProfile = 0;
    ImageView counter;

    public UsersAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Users user = users.get(position);
//***********************************************************
        //for Last msg and time
        String receiverId = user.getUid();
        seenMessage(receiverId);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();
        //retrieving the data from data base(lst msg) and set it to UI
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            //for Formatting the Time
                            AES aes = new AES(context);
                            String decryptMsg = aes.Decrypt(Objects.requireNonNull(lastMsg),context);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh : mm a");

                            holder.binding.msgTime.setText(TimeAgo.getFormattedDate(time));
                            holder.binding.lastMsg.setText(decryptMsg);
                        } else {
                            holder.binding.lastMsg.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });



//********************************************************************
        holder.binding.username.setText(user.getName());
        counter = holder.binding.counter;


        //holder.binding.profileImg.setImageResource(user.getProfileImage()); wrong xxx

        //Load UserImage to MainActivity List
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        database1.getReference().child("users").child(receiverId).child("privacy")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (snapshot.hasChild("dpProfile")){
                                String value = snapshot.child("dpProfile").getValue().toString();
                                if (value.equals("Nobody")){
                                    dpProfile =1;
                                    Glide.with(context)
                                            .load(R.drawable.avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .into(holder.binding.profile);
                                }
                                else if(value.equals("EveryOne")){
                                    dpProfile =0;
                                    database1.getReference().child("users").child("profileImage").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Glide.with(context).load(user.getProfileImage()).placeholder(R.drawable.avatar).into(holder.binding.profile);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }
                        else{
                            dpProfile=0;
                            database1.getReference().child("users").child("profileImage").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Glide.with(context).load(user.getProfileImage()).placeholder(R.drawable.avatar).into(holder.binding.profile);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        holder.binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dpProfile==0){
                    String profileImage = user.getProfileImage();
                    Intent intent = new Intent(context, imageViewer.class);
                    intent.putExtra("profileImage", profileImage);
                    intent.putExtra("userName", user.getName());
                    context.startActivity(intent);
                }
                else{
                    int profileImage = R.drawable.avatar;
                    Intent intent = new Intent(context, imageViewer.class);
                    intent.putExtra("profileImage", profileImage);
                    intent.putExtra("userName", user.getName());
                    context.startActivity(intent);
                }
            }
        });

        //when Clicked on Any Chat open Chat Activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dpProfile==0){
                    Intent intent = new Intent(context, chatActivity.class);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("image",user.getProfileImage());
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("email", user.getEmailId());
                    intent.putExtra("phone", user.getPhoneNumber());
                    intent.putExtra("statusText", user.getUid());
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, chatActivity.class);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("image", R.drawable.avatar);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("email", user.getEmailId());
                    intent.putExtra("phone", user.getPhoneNumber());
                    intent.putExtra("statusText", user.getUid());
                    context.startActivity(intent);
                }
            }
        });

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        String receiverUid = user.getUid();
        String senderUid = FirebaseAuth.getInstance().getUid();

        database2.getReference().child("presence").child(receiverUid).child(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String checker = snapshot.getValue().toString();
                            if (checker.equals("1")){
                                holder.binding.onlineToast.setText("typing...");
                            }
                            else{
                                database2.getReference().child("presence").child(receiverUid).child("privacy")
                                        .addValueEventListener(new ValueEventListener() {
                                            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.hasChild("onlineToast")){
                                                        String checker = snapshot.child("onlineToast").getValue().toString();
                                                        if(checker.equals("online")) {
                                                            holder.binding.onlineToast.setTextColor(R.color.onlineStatus);
                                                            holder.binding.onlineToast.setText("Online");
                                                        }
                                                        else if(snapshot.hasChild("lastSeenPrivacy")){
                                                            String seen = snapshot.child("lastSeenPrivacy").getValue().toString();
                                                            if(seen.equals("Nobody")){
                                                                holder.binding.onlineToast.setText("");
                                                            }
                                                            if(seen.equals("EveryOne")){
                                                                holder.binding.onlineToast.setTextColor(Color.BLACK);
                                                                holder.binding.onlineToast.setText("last Seen "+ TimeAgo.getFormattedLastSeen(Long.parseLong(checker)));
                                                            }
                                                        }
                                                        else{
                                                            holder.binding.onlineToast.setTextColor(Color.BLACK);
                                                            holder.binding.onlineToast.setText("last Seen "+ TimeAgo.getFormattedLastSeen(Long.parseLong(checker)));
                                                        }
                                                    }


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }


    private void seenMessage(String userID) {
        String senderRoom = FirebaseAuth.getInstance().getUid() + userID;
        String receiverRoom = userID + FirebaseAuth.getInstance().getUid();
        //Toast.makeText(chatActivity.this, "SeenMessageCalled", Toast.LENGTH_SHORT).show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .child("messages");
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    Message message = snapshot2.getValue(Message.class);
                    if (message.getReceiverId().equals(fuser.getUid()) && message.getSenderId().equals(userID)) {
                        // Toast.makeText(chatActivity.this, "True", Toast.LENGTH_SHORT).show();
                        message.setSeen(true);
                        database.getReference()
                                .child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .child(snapshot2.getKey())
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(snapshot2.getKey())
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void seenCounter(String userID) {
        String senderRoom = FirebaseAuth.getInstance().getUid() + userID;
        String receiverRoom = userID + FirebaseAuth.getInstance().getUid();
        //Toast.makeText(chatActivity.this, "SeenMessageCalled", Toast.LENGTH_SHORT).show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats")
                .child(receiverRoom)
                .child("messages");
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int unread =0;
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    Message message = snapshot2.getValue(Message.class);
                    if(snapshot2.exists()){

                        if(snapshot2.hasChild("seen")){
                            String value = snapshot2.child("seen").getValue().toString();
                            if(value.equals("false")){
                                unread++;
                            }

                        }


                    }
                }
                if (unread == 0){
                    counter.setVisibility(View.VISIBLE);
                }
                else{
                    counter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}
