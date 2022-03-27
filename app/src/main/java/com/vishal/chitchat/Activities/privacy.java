package com.vishal.chitchat.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.R;
import com.vishal.chitchat.biometrics.BiometricCallback;
import com.vishal.chitchat.databinding.ActivityPrivacyBinding;

import java.util.Objects;


public class privacy extends AppCompatActivity implements BiometricCallback {

    ActivityPrivacyBinding binding;
    public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";
    public String Authentication = "0";
    boolean switchAuthentication;
    TextView lastSeen, profilePhoto, about, status;

    SwitchCompat Swicth_authenticate;

    @SuppressLint({"UseSwitchCompatOrMaterialCode", "CommitPrefEdits", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lastSeen = binding.lastSeenTxt;
        profilePhoto = binding.profilePhotoTxt;
        about = binding.aboutTxt;
        status = binding.statusTxt;

        Swicth_authenticate = binding.fingerEnable;

        loadData();
        updateViews();

        SharedPreferences sharedPreferences = getSharedPreferences("Authentication", 0);
        String bio = sharedPreferences.getString(TEXT, "");
        if (bio.equals("1")){
            binding.fingerLockTxt.setText("Enable");
        }
        else{
            binding.fingerLockTxt.setText("Disable");
        }

        binding.backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            }
        });

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(privacy.this, editPrivacy.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
            }
        });

        Swicth_authenticate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    com.vishal.chitchat.biometrics.BiometricManager mBiometricManager = new com.vishal.chitchat.biometrics.BiometricManager.BiometricBuilder(privacy.this)
                            .setTitle("Biometric Authentication")
                            .setSubtitle("Place your finger to Sensor to continue")
                            .setDescription("Hi")
                            .setNegativeButtonText("Cancel")
                            .build();

                    mBiometricManager.authenticate(privacy.this);

                }
                else {
                    binding.fingerLockTxt.setText("Disabled");
                    SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TEXT, "0");
                    editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
                    editor.apply();
                }
            }
        });

        valueSetter();

    }

    @SuppressLint("SetTextI18n")
    private void Biometric(){
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK | androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {


            // this means we can use biometric sensor
            case androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS:


                SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT, "1");
                editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
                editor.apply();

                break;

            // this means that the device doesn't have fingerprint sensor
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Swicth_authenticate.setChecked(false);
                Toast.makeText(this, "Error code 0x08080101 Authentication failed there's no Fingerprint Reader in your device.", Toast.LENGTH_SHORT).show();
                break;

            // this means that biometric sensor is not available
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Swicth_authenticate.setChecked(false);
                Toast.makeText(this, "Error code 0x08080102 Authentication failed biometric system not found.", Toast.LENGTH_SHORT).show();
                break;

            // this means that the device doesn't contain your fingerprint
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Swicth_authenticate.setChecked(false);
                Toast.makeText(this, "Error code 0x08080103 There's no password for this device.", Toast.LENGTH_SHORT).show();
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
        }
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
        Authentication = sharedPreferences.getString(TEXT, "");
        switchAuthentication = sharedPreferences.getBoolean(SWITCH1,false);
    }

    @SuppressLint("SetTextI18n")
    public void updateViews(){
        Swicth_authenticate.setChecked(switchAuthentication);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSdkVersionNotSupported() {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBiometricAuthenticationNotSupported() {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAuthenticationFailed() {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAuthenticationCancelled() {
        Swicth_authenticate.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAuthenticationSuccessful() {
        Biometric();
        binding.fingerLockTxt.setText("Enable");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void valueSetter(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String currentId = FirebaseAuth.getInstance().getUid();

        //for last seen update
        database.getReference().child("presence").child(Objects.requireNonNull(currentId)).child("privacy").child("lastSeenPrivacy")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String value = snapshot.getValue().toString();
                            if (value.equals("Nobody")){
                                lastSeen.setText("Nobody");
                            }
                            else{
                                lastSeen.setText("EveryOne");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //for profile photo update
        database.getReference().child("users").child(currentId).child("privacy").child("dpProfile")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String value = snapshot.getValue().toString();
                            if (value.equals("EveryOne")){
                                profilePhoto.setText("EveryOne");
                            }else{
                                profilePhoto.setText("Nobody");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //for about status
        database.getReference().child("users").child(currentId).child("privacy").child("about")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String value = snapshot.getValue().toString();
                            if (value.equals("EveryOne")){
                                about.setText("EveryOne");
                            }else{
                                about.setText("Nobody");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //for phone number
        database.getReference().child("users").child(currentId).child("privacy").child("phoneNo")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String value = snapshot.getValue().toString();
                            if (value.equals("EveryOne")){
                                status.setText("EveryOne");
                            }else{
                                status.setText("Nobody");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}