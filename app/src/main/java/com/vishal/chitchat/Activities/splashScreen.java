package com.vishal.chitchat.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.vishal.chitchat.R;
import com.vishal.chitchat.biometrics.BiometricCallback;
import com.vishal.chitchat.databinding.ActivitySplashScreenBinding;



public class splashScreen extends AppCompatActivity implements BiometricCallback {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    //for animation
    ActivitySplashScreenBinding binding;
    Animation topAnim, bottomAnim;
    public static final String TEXT = "text";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
protected void onStart() {

    super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("Authentication", 0);
        String bio = sharedPreferences.getString(TEXT, "");
        if(auth.getCurrentUser() != null) {
            if (bio.equals("1")) {


                com.vishal.chitchat.biometrics.BiometricManager mBiometricManager = new com.vishal.chitchat.biometrics.BiometricManager.BiometricBuilder(splashScreen.this)
                        .setTitle("Biometric Authentication")
                        .setSubtitle("Place your finger to Sensor to continue")
                        .setDescription("Hi")
                        .setNegativeButtonText("Cancel")
                        .build();

                mBiometricManager.authenticate(splashScreen.this);

            }
            else {
                Thread t1 = new Thread() {

                    public void run() {
                        try {
                            sleep(1900);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Intent intent = new Intent(splashScreen.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                            finish();
                        }
                    }
                };
                t1.start();
            }
        }
        else {
            Thread t1 = new Thread() {

                public void run() {
                    try {
                        sleep(1900);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent = new Intent(splashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                        finish();
                    }
                }
            };
            t1.start();
        }

}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //for hiding title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //for anim
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        binding.loginAnimation.setAnimation(topAnim);
        binding.appName.setAnimation(bottomAnim);
        binding.tagline.setAnimation(bottomAnim);


    }


    @Override
    public void onSdkVersionNotSupported() {
        Toast.makeText(splashScreen.this, "Not supported", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBiometricAuthenticationNotSupported() {
        Toast.makeText(splashScreen.this, "Not supported", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Toast.makeText(splashScreen.this, "Not Available", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(splashScreen.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        finishAffinity();
    }

    @Override
    public void onAuthenticationFailed() {
    }

    @Override
    public void onAuthenticationCancelled() {

    }

    @Override
    public void onAuthenticationSuccessful() {
        Intent intent = new Intent(splashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

    }
}