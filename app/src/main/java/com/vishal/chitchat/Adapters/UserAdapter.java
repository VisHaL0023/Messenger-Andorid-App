package com.vishal.chitchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.vishal.chitchat.Models.Follow;
import com.vishal.chitchat.Models.Notification;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.UserSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    Context context;
    ArrayList<Users> list;

    public UserAdapter(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Users user = list.get(position);

        //Set user profile
        Picasso.get()
                .load(user.getProfileImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.profileImage);
        holder.binding.name.setText(user.getName());
        holder.binding.profession.setText(user.getStatusText());

        //Check already following or not
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getUid())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Change background and text
                    //your action...
                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                    holder.binding.followBtn.setText("Following");
                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.gray));
                    holder.binding.followBtn.setEnabled(false);
                } else {
                    holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Set data
                            Follow follow = new Follow();
                            follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                            follow.setFollowedAt(new Date().getTime());

                            //Store follower data in database
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(user.getUid())
                                    .child("followers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Follower count
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("users")
                                            .child(user.getUid())
                                            .child("followerCount")
                                            .setValue(user.getFolllowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Change background and text
                                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                                            holder.binding.followBtn.setText("Following");
                                            holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.gray));
                                            holder.binding.followBtn.setEnabled(false);
                                            Toast.makeText(context, "You Followed " + user.getName(), Toast.LENGTH_SHORT).show();

                                            //Set Notification data to Notification object
                                            Notification notification = new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setType("follow");

                                            //Save notification data in database
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("notification")
                                                    .child(user.getUid())
                                                    .push()
                                                    .setValue(notification);

                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        UserSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserSampleBinding.bind(itemView);
        }
    }
}
