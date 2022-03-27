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
import com.vishal.chitchat.databinding.ActivityUserDataBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class userData extends AppCompatActivity {

    ActivityUserDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Users users;
                        users = snapshot.getValue(Users.class);

                        binding.userName.setText(users.getName());
                        Glide.with(userData.this)
                                .load(users.getProfileImage())
                                .into(binding.userProfile);
                        binding.userStatus.setText(users.getStatusText());
                        binding.emailId.setText(users.getEmailId());
                        binding.phoneNumber.setText(users.getPhoneNumber());


                        binding.userProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String profileImage = users.getProfileImage();
                                Intent intent = new Intent(userData.this, imageViewer.class);
                                intent.putExtra("userName", users.getName());
                                intent.putExtra("profileImage", profileImage);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
                binding.userNo.setText(userCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            }
        });
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //for showing the Online Status of the User
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).child("privacy").child("onlineToast").setValue("online");
    }


    Users user1 = new Users();
    Date date = new Date();
    @Override
    protected void onPause() {
        super.onPause();
        user1.setTimestamp(date.getTime());
        String lastUpdate = String.valueOf(user1.getTimestamp());
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).child("privacy").child("onlineToast").setValue(lastUpdate);
    }

}