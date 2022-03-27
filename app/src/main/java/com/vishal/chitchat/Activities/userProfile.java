package com.vishal.chitchat.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivityUserProfileBinding;

import java.util.Date;
import java.util.Objects;

public class userProfile extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getIntent from chat activity
        String name = getIntent().getStringExtra("name");
        // For getting the Profile Image for toolBar
        String profilePic = getIntent().getStringExtra("profilePic");
        String emailId = getIntent().getStringExtra("emailId");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String statusText = getIntent().getStringExtra("statusText");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("users").child(statusText).child("privacy").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("about")){
                        String value = snapshot.child("about").getValue().toString();
                        if(value.equals("Nobody")){
                            binding.userStatus.setText("Not Showed Due to Privacy");
                        }
                        else if (value.equals("EveryOne")){
                            database.getReference().child("users").child(statusText).child("statusText")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                String status = snapshot.getValue().toString();
                                                binding.userStatus.setText(status);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.emailId.setText(emailId);
        binding.userName.setText(name);
        binding.phoneBox.setText(phoneNumber);
        Glide.with(userProfile.this)
                .load(profilePic)
                .placeholder(R.drawable.avatar)
                .into(binding.userProfile);

        binding.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileImage = profilePic;
                Intent intent = new Intent(userProfile.this, imageViewer.class);
                intent.putExtra("userName", name);
                intent.putExtra("profileImage", profileImage);
                startActivity(intent);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            }
        });

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter =0;
                counter = (int) snapshot.getChildrenCount();

                //Convert counter to string
                String userCounter = String.valueOf(counter);
                binding.totalUser.setText("Total Users are  " +userCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //for showing the Online Status of the User
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(Objects.requireNonNull(currentId)).child("privacy").child("onlineToast").setValue("online");
    }


    Users user1 = new Users();
    Date date = new Date();
    @Override
    protected void onPause() {
        super.onPause();
        user1.setTimestamp(date.getTime());
        String lastUpdate = String.valueOf(user1.getTimestamp());
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(Objects.requireNonNull(currentId)).child("privacy").child("onlineToast").setValue(lastUpdate);
    }
}