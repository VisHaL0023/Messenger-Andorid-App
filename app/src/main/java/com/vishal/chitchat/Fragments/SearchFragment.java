package com.vishal.chitchat.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Adapters.UserAdapter;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Objects;


public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        //Show Shimmer effect until data loads
        binding.usersRV.showShimmerAdapter();

        //Users Recycler View
        UserAdapter adapter = new UserAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.usersRV.setLayoutManager(layoutManager);

        //Fetching Users data from database and set in user recyclerview
        database.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    Objects.requireNonNull(user).setUid(dataSnapshot.getKey());
                    if (!Objects.equals(dataSnapshot.getKey(), FirebaseAuth.getInstance().getUid())) {
                        list.add(user);
                    }
                }
                binding.usersRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //Hide shimmer effect when data loaded
                binding.usersRV.hideShimmerAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}