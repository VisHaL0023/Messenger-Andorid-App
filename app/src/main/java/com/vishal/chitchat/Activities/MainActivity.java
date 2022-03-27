package com.vishal.chitchat.Activities;

import static com.google.firebase.messaging.FirebaseMessaging.getInstance;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vishal.chitchat.Fragments.HomeFragment;
import com.vishal.chitchat.Fragments.Profile2Fragment;
import com.vishal.chitchat.Fragments.SearchFragment;
import com.vishal.chitchat.Fragments.StatusViewer;
import com.vishal.chitchat.Fragments.callFragment;
import com.vishal.chitchat.Fragments.chatFragment;
import com.vishal.chitchat.Fragments.settingProfileFregment;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity  {

    //Variable Declaration
    ActivityMainBinding binding;
    FirebaseDatabase database;
    String currentId;
    Users user;

    //for saving state of frag
    Fragment activeFrag;
    final Fragment chatFragment = new chatFragment();
    final Fragment callFragment = new callFragment();
    final Fragment homeFragment = new HomeFragment();
    final Fragment searchFragment = new SearchFragment();
    final Fragment profile = new Profile2Fragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


      //for preventing again again reloading of frags
        fragmentManager.beginTransaction().add(binding.activitymainLinearLayout.getId(), homeFragment).commit();
        activeFrag = homeFragment;
        fragmentManager.beginTransaction().add(binding.activitymainLinearLayout.getId(), chatFragment).hide(chatFragment).commit();

        fragmentManager.beginTransaction().add(binding.activitymainLinearLayout.getId(), callFragment).hide(callFragment).commit();
        fragmentManager.beginTransaction().add(binding.activitymainLinearLayout.getId(), searchFragment).hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(binding.activitymainLinearLayout.getId(), profile).hide(profile).commit();


        currentId = FirebaseAuth.getInstance().getUid();

        database = FirebaseDatabase.getInstance();


        //selecting option from Bottom NAV

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if (binding.bottomNavigationView.getSelectedItemId() != item.getItemId()) {

                    switch (item.getItemId()) {

                        case R.id.home:
                            fragmentManager.beginTransaction().hide(activeFrag).show(homeFragment).commit();
                            activeFrag = homeFragment;
                            break;

                        case R.id.chats:
                            fragmentManager.beginTransaction().hide(activeFrag).show(chatFragment).commit();
                            activeFrag = chatFragment;
                            break;
                        case R.id.search:
                            fragmentManager.beginTransaction().hide(activeFrag).show(searchFragment).commit();
                            activeFrag = searchFragment;
                            break;

                        case R.id.calls:
                            fragmentManager.beginTransaction().hide(activeFrag).show(callFragment).commit();
                            activeFrag = callFragment;
                            break;

                        case R.id.profileMain:
                            fragmentManager.beginTransaction().hide(activeFrag).show(profile).commit();
                            activeFrag = profile;
                            break;
                    }
                }
                return true;
            }
        });

        //For Firebase cloud Messaging Token Generation
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Token generation Failed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("FCM").setValue(token);
                        }
                    });
        }


    }



    //for showing the Online Status of the User
    @Override
    protected void onResume() {
        super.onResume();
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
                    database.getReference().child("presence").child(Objects.requireNonNull(currentId)).child("privacy").child("onlineToast").setValue(lastUpdate);
    }

}