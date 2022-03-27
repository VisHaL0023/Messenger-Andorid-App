package com.vishal.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Activities.chatActivity;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.Network.UsersListener;
import com.vishal.chitchat.R;
import com.vishal.chitchat.Services.TimeAgo;
import com.vishal.chitchat.databinding.CallLayoutBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder>{

    Context context;
    ArrayList<Users> users;
    UsersListener usersListener;



    public CallAdapter(Context context, ArrayList<Users> users, UsersListener usersListener) {
        this.context = context;
        this.users = users;
        this.usersListener = usersListener;
    }

    @NonNull
    @NotNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.call_layout,parent,false);
        return new CallViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CallAdapter.CallViewHolder holder, int position) {
         Users user = users.get(position);

         holder.binding.calleruserName.setText(user.getName());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String callValue = user.getUid()+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        database.getReference().child("callTime").child(callValue).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long callTime = snapshot.getValue(long.class);
                    holder.binding.lastCall.setText("Last call "+ TimeAgo.getFormattedLastSeen(callTime));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Glide.with(context).load(user.getProfileImage()).placeholder(R.drawable.avatar).into(holder.binding.callerprofileImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, chatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("image",user.getProfileImage());
                intent.putExtra("uid", user.getUid());
                intent.putExtra("email", user.getEmailId());
                intent.putExtra("phone", user.getPhoneNumber());
                intent.putExtra("statusText", user.getStatusText());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class CallViewHolder extends RecyclerView.ViewHolder
    {
        CallLayoutBinding binding;
        public CallViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = CallLayoutBinding.bind(itemView);

            //setting call on button
            binding.callImageButtom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     //for getting the user current in adapter
                    Users user = users.get(getAdapterPosition());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    Date date = new Date();
                    String callValue = user.getUid()+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    user.setLastCallTime(date.getTime());
                    long callTime = user.getLastCallTime();
                    database.getReference().child("callTime").child(callValue).setValue(callTime);

                       usersListener.initiateAudioMeeting(user);

                }
            });

            binding.videoCallImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Users user = users.get(getAdapterPosition());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    Date date = new Date();
                    String callValue = user.getUid()+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    user.setLastCallTime(date.getTime());
                    long callTime = user.getLastCallTime();
                    database.getReference().child("callTime").child(callValue).setValue(callTime);

                    usersListener.initiateVideoMeeting(user);
                }
            });

        }
    }
}
