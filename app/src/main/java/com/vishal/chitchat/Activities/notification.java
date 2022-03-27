package com.vishal.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Adapters.NotificationAdapter;
import com.vishal.chitchat.Models.Notification;
import com.vishal.chitchat.databinding.ActivityNotificationBinding;

import java.util.ArrayList;

public class notification extends AppCompatActivity {

    ActivityNotificationBinding binding;
    RecyclerView notificationRV;
    ArrayList<Notification> list;
    FirebaseDatabase database;


    public notification(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        notificationRV = binding.notificationRV;
        //Start Notification Recycler View
        list = new ArrayList<>();
        NotificationAdapter adapter = new NotificationAdapter(list, notification.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(notification.this,LinearLayoutManager.VERTICAL,true);
        notificationRV.setLayoutManager(layoutManager);
        notificationRV.setNestedScrollingEnabled(false);
        notificationRV.setAdapter(adapter);



        //Fetching Notification data from database and set in notification recyclerview
        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notification.setNotificationID(dataSnapshot.getKey());
                            list.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });//End of Notification Recycler View

    }
}