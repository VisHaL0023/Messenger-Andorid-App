package com.vishal.chitchat.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivitySetUpProfileBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class setUpProfileActivity extends AppCompatActivity {

    //Variables Declaration
    ActivitySetUpProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetUpProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Profile");
        dialog.setCancelable(false);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 45);
        }



            binding.imageView.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("IntentReset")
                @Override
                public void onClick(View v) {
                    @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX", 256);
                    intent.putExtra("outputY", 256);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, 45);
                }
            });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameBox.getText().toString();
                String emailId = binding.emailId.getText().toString();
                String password = binding.password.getText().toString();

                loading(true);
                if (selectedImage != null) {
                    if (name.isEmpty()) {
                        loading(false);
                        binding.nameBox.setError("Enter Name");
                    } else if (!isValidEmail(emailId)) {
                        loading(false);
                        binding.emailId.setError("Enter a Valid Email");
                    } else if (!isValidPassword(password)) {
                        loading(false);
                        binding.password.setError("Enter a Strong Password");
                    }
                    auth.createUserWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String imageUrl = uri.toString();
                                                String uid = auth.getUid();
                                                String phone;
                                                if(auth.getCurrentUser().getPhoneNumber() != null){
                                                    phone = auth.getCurrentUser().getPhoneNumber();
                                                }
                                                else {
                                                    phone = getIntent().getStringExtra("phoneNumber");
                                                }
                                                String name = binding.nameBox.getText().toString();
                                                String emailId = binding.emailId.getText().toString();
                                                String password = binding.password.getText().toString();
                                                String statusText = binding.status.getText().toString();


                                                Users user = new Users(uid, name, phone, imageUrl, emailId, password, statusText);

                                                //adding User into Database
                                                database.getReference().child("users").child(uid).setValue(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                loading(false);
                                                                Intent intent = new Intent(setUpProfileActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                                                                finish();
                                                            }
                                                        });
                                            }
                                        });
                                    } else {
                                        loading(false);
                                        Toast.makeText(setUpProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }
                else {
                    loading(false);
                    Toast.makeText(setUpProfileActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {
                binding.imageView.setImageURI(data.getData());
                selectedImage = data.getData();

            }
        }
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


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 45:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;

            default:
                break;
        }
    }

}