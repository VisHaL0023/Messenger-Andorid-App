package com.vishal.chitchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivityEditPrivacyBinding;

import java.util.Date;
import java.util.Objects;

public class editPrivacy extends AppCompatActivity {

    ActivityEditPrivacyBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
                finish();
            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                if(binding.lastSeenGroup.getCheckedRadioButtonId() ==-1 || binding.ProfilePhotoGroup.getCheckedRadioButtonId() ==-1 || binding.AboutGroup.getCheckedRadioButtonId() ==-1 || binding.PhoneGroup.getCheckedRadioButtonId() ==-1 ){
                    Toast.makeText(editPrivacy.this, "Please Select all fields", Toast.LENGTH_SHORT).show();
                    loading(false);
                }
                else{
                    int lastSeen = binding.lastSeenGroup.getCheckedRadioButtonId();
                    int profilePhoto = binding.ProfilePhotoGroup.getCheckedRadioButtonId();
                    int about = binding.AboutGroup.getCheckedRadioButtonId();
                    int status = binding.PhoneGroup.getCheckedRadioButtonId();
                    RadioButton selectedLastSeen = (RadioButton)findViewById(lastSeen);
                    RadioButton selectedProfilePhoto = (RadioButton)findViewById(profilePhoto);
                    RadioButton selectedAbout = (RadioButton)findViewById(about);
                    RadioButton selectedStatus = (RadioButton)findViewById(status);

                    String LastSeen = selectedLastSeen.getText().toString();
                    String ProfileSeen = selectedProfilePhoto.getText().toString();
                    String About = selectedAbout.getText().toString();
                    String phonePrivacy = selectedStatus.getText().toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String currentId = FirebaseAuth.getInstance().getUid();
                    database.getReference().child("presence").child(Objects.requireNonNull(currentId)).child("privacy").child("lastSeenPrivacy")
                            .setValue(LastSeen);

                    database.getReference().child("users").child(currentId).child("privacy").child("dpProfile")
                            .setValue(ProfileSeen);

                    database.getReference().child("users").child(currentId).child("privacy").child("about")
                            .setValue(About);

                    database.getReference().child("users").child(currentId).child("privacy").child("phoneNo")
                            .setValue(phonePrivacy).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(editPrivacy.this, "Privacy Updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(editPrivacy.this, privacy.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);

                        }
                    });
                    loading(false);

                }
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


    private void loading(Boolean isLoading){
        if(isLoading){
            binding.continueBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.continueBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}