package com.vishal.chitchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {

    //Variable Declarations
    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to automatically pop up keyboard
        binding.phoneBox.requestFocus();
        binding.phoneBox.setText("+91");
        binding.phoneBox.setSelection(3);

        auth = FirebaseAuth.getInstance();

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                String number = binding.phoneBox.getText().toString();
                if(number.length() < 13)
                {
                    loading(false);
                    binding.phoneBox.setError("Enter a Valid Phone Number(Make Sure to Add +91 code)");
                }
                else
                {
                    loading(false);
                    Intent intent = new Intent(PhoneNumberActivity.this, otpActivity.class);
                    intent.putExtra("phoneNumber", number);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                }

            }
        });
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