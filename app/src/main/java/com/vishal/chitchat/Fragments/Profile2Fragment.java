package com.vishal.chitchat.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vishal.chitchat.Activities.LoginActivity;
import com.vishal.chitchat.Activities.MainActivity;
import com.vishal.chitchat.Activities.editPrivacy;
import com.vishal.chitchat.Activities.privacy;
import com.vishal.chitchat.Adapters.FollowersAdapter;
import com.vishal.chitchat.Models.Follow;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.FragmentProfile2Binding;
import com.squareup.picasso.Picasso;
import com.vishal.chitchat.Adapters.FollowersAdapter;
import com.vishal.chitchat.databinding.FragmentProfile2Binding;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Profile2Fragment extends Fragment {

    ArrayList<Follow> list;
    FragmentProfile2Binding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    public Profile2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = FragmentProfile2Binding.inflate(inflater, container, false);

        //My followers Recycler View
        list = new ArrayList<>();
        FollowersAdapter adapter = new FollowersAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.followersRV.setLayoutManager(layoutManager);
        binding.followersRV.setAdapter(adapter);

        //Fetching followers data from database and set in recyclerview
        database.getReference().child("users")
                .child(Objects.requireNonNull(auth.getUid()))
                .child("followers").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Follow follow = dataSnapshot.getValue(Follow.class);
                    list.add(follow);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//End of Followers Recycler View


        //Fetch User data from database
        database.getReference().child("users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    //Set User cover image
                    Picasso.get()
                            .load(Objects.requireNonNull(user).getCoverPhoto())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.coverPhoto);
                    //Set User profile image
                    Picasso.get()
                            .load(user.getProfileImage())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.profileImage);
                    //Set user info
                    binding.userName.setText(user.getName());
                    binding.profession.setText(user.getStatusText());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("users").child(auth.getUid()).child("followerCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String value = snapshot.getValue().toString();
                            binding.followers.setText(value);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Upload Cover Image from gallery
        binding.changeCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open gallery using Intent
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });

        //Upload Profile Image from gallery
        binding.verifiedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open gallery using Intent
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 22);
            }
        });



        binding.menuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.setting, popup.getMenu());
                Menu menuOpts = popup.getMenu();


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){

                            case R.id.SignOut:

                                String currentId = FirebaseAuth.getInstance().getUid();

                                new AlertDialog.Builder(getContext())
                                        .setTitle("Logout")
                                        .setMessage("Do you really want to Logout?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            @SuppressLint("StaticFieldLeak")
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                Toast.makeText(getContext(), "Logging Out...", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                if (auth.getCurrentUser() != null) {

                                                    ExecutorService executor = Executors.newSingleThreadExecutor();
                                                    Handler handler = new Handler(Looper.getMainLooper());

                                                    executor.execute(() -> {
                                                        //Background work here

                                                        FirebaseMessaging.getInstance().deleteToken();
                                                        firebaseDatabase.getReference().child("users").child(currentId)
                                                                .child("FCM").removeValue();

                                                        handler.post(() -> {
                                                            //UI Thread work here

                                                            auth.signOut();
                                                            startActivity(new Intent(getContext(), LoginActivity.class));

                                                        });
                                                    });

                                                }

                                            }}).setNegativeButton(android.R.string.no, null).show();

                                return true;

                            case R.id.privacy:

                                Intent intent = new Intent(getContext(), privacy.class);
                                startActivity(intent);
                                return true;

                            case R.id.twoStep:
                                Toast.makeText(getContext(), "Two Step Clicked", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.deleteAccount:

                                new AlertDialog.Builder(getContext())
                                        .setTitle("Delete Account")
                                        .setMessage("Do you really want to Delete Account?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            @SuppressLint("StaticFieldLeak")
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                                Objects.requireNonNull(currentUser).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @SuppressLint("LogNotTimber")
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            database.getReference().child("users").child(Objects.requireNonNull(firebaseAuth.getUid()))
                                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getContext(), "Deleting your data....", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            database.getReference().child("stories").child(Objects.requireNonNull(firebaseAuth.getUid()))
                                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    firebaseAuth.signOut();
                                                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            });



                                                        }
                                                        else {
                                                            Log.w("TAG","Something is wrong!");
                                                        }
                                                    }
                                                });

                                            }}).setNegativeButton(android.R.string.no, null).show();

                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Differentiate user select profile or cover through request code
        if (requestCode == 11){
            //Cover Photo
            if (data.getData()!= null){
                Uri uri = data.getData();
                //set cover photo user select from gallery
                binding.coverPhoto.setImageURI(uri);

                //Define storage reference for cover photo in Firebase Storage
                final StorageReference reference = storage.getReference().child("cover_photo")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                //Store image in reference you define above
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover photo saved", Toast.LENGTH_SHORT).show();
                        //Copy Cover Image From Firebase storage to Firebase Database
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Store cover photo in database in Users node
                                database.getReference().child("users").child(Objects.requireNonNull(auth.getUid())).child("coverPhoto").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }else {
            //Profile Image
            if (data.getData()!= null){
                Uri uri = data.getData();
                //set profile image user select from gallery
                binding.profileImage.setImageURI(uri);

                //Define storage reference for profile image in Firebase Storage
                final StorageReference reference = storage.getReference().child("profile_image")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                //Store image in reference you define above
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Profile photo saved", Toast.LENGTH_SHORT).show();
                        //Copy Profile Image From Firebase storage to Firebase Database
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Store profile image in database in Users node
                                database.getReference().child("users").child(Objects.requireNonNull(auth.getUid())).child("profile").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }

    }
}