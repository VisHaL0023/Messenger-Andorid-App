package com.vishal.chitchat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Activities.CommentActivity;
import com.vishal.chitchat.Activities.imageViewer;
import com.vishal.chitchat.Models.Notification;
import com.vishal.chitchat.Models.Post;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.DashboardRvDesignBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<Post> list;
    Context context;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Post model = list.get(position);
        //Set post image
        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.postImage);

        Zoomy.Builder builder = new Zoomy.Builder((Activity) context).target(holder.binding.postImage);
        builder.register();




        //set post other info
        holder.binding.like.setText(model.getPostLike() + "");
        holder.binding.comment.setText(model.getCommentCount() + "");
        String description = model.getPostDescription();
        if (description.equals("")) {
            //hide description if not available
            holder.binding.postDescription.setVisibility(View.GONE);
        } else {
            holder.binding.postDescription.setText(model.getPostDescription());
            holder.binding.postDescription.setVisibility(View.VISIBLE);
        }


        //Getting User data from database based on PostedBy
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                //Set user profile
                Picasso.get()
                        .load(Objects.requireNonNull(user).getProfileImage())
                        .placeholder(R.drawable.avatar)
                        .into(holder.binding.profileImage);
                holder.binding.userName.setText(user.getName());
                holder.binding.bio.setText(user.getStatusText());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //LIKE POST
        //Check if User already liked post
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //If already liked simply changed drawable
                            //perform your action
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_2, 0, 0, 0);
                        } else {
                            //If Not liked yet
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Save user id who liked in database
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Increment by 1 in existing likes
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("posts")
                                                    .child(model.getPostId())
                                                    .child("postLike")
                                                    .setValue(model.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_2, 0, 0, 0);

                                                    //Set Notification data to Notification object
                                                    Notification notification = new Notification();
                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                    notification.setNotificationAt(new Date().getTime());
                                                    notification.setPostID(model.getPostId());
                                                    notification.setPostedBy(model.getPostedBy());
                                                    notification.setType("like");

                                                    //Save notification data in database
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("notification")
                                                            .child(model.getPostedBy())
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

        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open comment activity
                Intent intent = new Intent(context, CommentActivity.class);
                //Send data to Comment activity through Intent
                intent.putExtra("postId", model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        DashboardRvDesignBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvDesignBinding.bind(itemView);
        }
    }
}
