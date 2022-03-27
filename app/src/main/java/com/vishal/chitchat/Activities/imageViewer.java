package com.vishal.chitchat.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivityImageViewerBinding;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public class imageViewer extends AppCompatActivity {

    ActivityImageViewerBinding binding;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingBar = new ProgressDialog(imageViewer.this);

        String getImage = getIntent().getStringExtra("profileImage");
        String userName = getIntent().getStringExtra("userName");

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            }
        });

        binding.userName.setText(userName);
        Glide.with(imageViewer.this)
                .load(getImage)
                .into(binding.imgDisplay);


        binding.shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareImageandText(getImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(imageViewer.this,"Something went wrong! Error Code 0x08040204",Toast.LENGTH_SHORT).show();


                }

            }
        });


        binding.setAsWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iOSDialog.Builder
                        .with(imageViewer.this)
                        .setTitle("Set Wallpaper")
                        .setMessage("Are you sure you want to set this image as wallpaper")
                        .setPositiveText("Yes")
                        .setPostiveTextColor(getApplicationContext().getResources().getColor(R.color.red))
                        .setNegativeText("Cancel")
                        .setNegativeTextColor(getApplicationContext().getResources().getColor(R.color.company_blue))
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                try {
                                    setWallpaper(getImage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do Nothing
                            }
                        })
                        .isCancellable(true)
                        .build()
                        .show();

            }
        });

        binding.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar.setTitle("Downloading");
                loadingBar.setMessage("Downloading and saving your image in gallery");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                DownloadImage(getImage,userName);
            }
        });


//        binding.profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                iOSDialog.Builder
//                        .with(imageViewer.this)
//                        .setTitle("Profile Picture")
//                        .setMessage("Are you sure you want to keep this image as your profile picture")
//                        .setPositiveText("Yes")
//                        .setPostiveTextColor(getResources().getColor(R.color.red))
//                        .setNegativeText("Cancel")
//                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
//                        .onPositiveClicked(new iOSDialogListener() {
//                            @Override
//                            public void onClick(Dialog dialog) {
//                                setProfilePicture(getImage);
//                            }
//                        })
//                        .onNegativeClicked(new iOSDialogListener() {
//                            @Override
//                            public void onClick(Dialog dialog) {
//                                //Do Nothing
//                            }
//                        })
//                        .isCancellable(true)
//                        .build()
//                        .show();
//
//
//            }
//        });


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


    private void setProfilePicture(String imageUri) {

        loadingBar.setTitle("Profile Image");
        loadingBar.setMessage("Please wait, while we update your profile picture...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);


    }


    private void shareImageandText(String imageurl) throws IOException {

        URL url = new URL(imageurl);
        Bitmap image;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            Toast.makeText(imageViewer.this, "Please Wait image is downloading...", Toast.LENGTH_SHORT).show();
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Image Description", null);
            Uri uri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Share Image"));
        }

    }

    private void DownloadImage(String imageurl,String sendername) {

        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here
                Toast.makeText(imageViewer.this, "Please Wait image is downloading...", Toast.LENGTH_SHORT).show();
                String title = randomString(6);
                URL url = new URL(imageurl);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                MediaStore.Images.Media.insertImage(getContentResolver(), image, title, sendername + "sent you this image in Messenger");


            }

        } catch(IOException e) {
            System.out.println(e);
            Toast.makeText(imageViewer.this,"Something went wrong! Error Code 0x08040201",Toast.LENGTH_SHORT).show();

        }


        loadingBar.dismiss();

    }

    private void setWallpaper(String imageurl) throws IOException {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            Toast.makeText(imageViewer.this, "Please Wait image is downloading...", Toast.LENGTH_SHORT).show();
            URL url = new URL(imageurl);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
            try{
                manager.setBitmap(image);
                Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error! 0x08040202", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static Random RANDOM = new Random();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }

}