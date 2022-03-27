package com.vishal.chitchat.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vishal.chitchat.Adapters.NotificationAdapter;
import com.vishal.chitchat.Models.Notification;
import com.vishal.chitchat.R;

import java.util.ArrayList;


public class RequestFrag extends Fragment {

    RecyclerView requestRv;
    ArrayList<Notification> list;

    public RequestFrag() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        requestRv = view.findViewById(R.id.requestRecyclerView);

        list = new ArrayList<>();


        NotificationAdapter adapter = new NotificationAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        requestRv.setLayoutManager(layoutManager);
        requestRv.setNestedScrollingEnabled(false);
        requestRv.setAdapter(adapter);

        return view;
    }
}