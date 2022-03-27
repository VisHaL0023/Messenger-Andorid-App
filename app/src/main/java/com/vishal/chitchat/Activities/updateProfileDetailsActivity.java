package com.vishal.chitchat.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.databinding.ActivityUpdateProfileDetailsBinding;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class updateProfileDetailsActivity extends AppCompatActivity {

    ActivityUpdateProfileDetailsBinding binding;
    Uri selectedImage;
    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage;

    @SuppressLint("IntentReset")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Users users = new Users();
        binding.phoneBox.setText(users.getPhoneNumber());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();


        binding.imageView.setOnClickListener(v -> {
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
            });
        binding.progressBar.setVisibility(View.GONE);


        binding.continueBtn.setOnClickListener(v -> {
            loading(true);

            if(selectedImage != null) {
                StorageReference reference = storage.getReference().child("Profiles").child(Objects.requireNonNull(auth.getUid()));
                reference.putFile(selectedImage).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            String name = binding.Name.getText().toString();
                            String statusText = binding.status.getText().toString();

                            Map<String, Object> map= new HashMap<>();
                            map.put("statusText", statusText);
                            map.put("name", name);

                            database.getReference().child("users")
                                    .child(auth.getUid())
                                    .updateChildren(map)
                                    .addOnCompleteListener(task1 -> {
                                        loading(false);
                                        Toast.makeText(updateProfileDetailsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    }
                    else{
                        Toast.makeText(updateProfileDetailsActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 45) {
            if (data != null) {
                if (data.getData() != null) {
                    binding.textAddImage.setVisibility(View.GONE);
                    String url = data.getData().toString();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    long time = new Date().getTime();
                    StorageReference reference = storage.getReference().child("Profiles").child(time + "");
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    reference.putFile(Uri.parse(url)).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                String filePath = uri1.toString();
                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put("image", filePath);
                                database.getReference().child("users")
                                        .child(Objects.requireNonNull(auth.getUid()))
                                        .updateChildren(obj).addOnSuccessListener(aVoid -> {

                                });
                            });
                        }
                    });


                    binding.imageView.setImageURI(data.getData());
                    selectedImage = data.getData();
                }
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