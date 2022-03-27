package com.vishal.chitchat.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.vishal.chitchat.Activities.LoginActivity;
import com.vishal.chitchat.Activities.privacy;
import com.vishal.chitchat.Activities.userData;
import com.vishal.chitchat.databinding.FragmentSettingProfileFregmentBinding;

import java.util.Objects;

public class settingProfileFregment extends Fragment {


    public settingProfileFregment() {
        // Required empty public constructor
    }

    FragmentSettingProfileFregmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingProfileFregmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        binding.Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), userData.class);
                startActivity(intent);
            }
        });

        binding.privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), privacy.class);
                startActivity(intent);
            }
        });

        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

        return view;
    }

}