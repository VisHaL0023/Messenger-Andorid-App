package com.vishal.chitchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishal.chitchat.Adapters.CommentAdapter;
import com.vishal.chitchat.Models.Comment;
import com.vishal.chitchat.Models.Notification;
import com.vishal.chitchat.Models.Post;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    Intent intent ;
    String postId;
    String postedBy;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Comment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        //Setting Custom toolbar
        setSupportActionBar(binding.toolbar3);
        //Set title in toolbar
        CommentActivity.this.setTitle("Comments");
        //show back button in toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        //Getting data we pass in PostAdapter through intent
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        //Fetch Post data from database
        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                //Set post image
                Picasso.get()
                        .load(post.getPostImage())
                        .placeholder(R.drawable.placeholder)
                        .into(binding.postImage);
                //Set post info
                binding.like.setText(post.getPostLike()+"");
                binding.comment.setText(post.getCommentCount()+"");
                String description = post.getPostDescription();
                if (description.equals("")){
                    binding.description.setVisibility(View.GONE);
                }else {
                    binding.description.setText(description);
                    binding.description.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//End of post data


        //Fetch User data from database
        database.getReference()
                .child("users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                //Set User profile
                Picasso.get()
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.placeholder)
                        .into(binding.profileImage);
                // set User name
                binding.name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.commnetPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set Comment data to Comment object
                Comment comment = new Comment();
                comment.setCommentBody(binding.commentET.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());

                //Save Comment data in database
                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Comment Count
                        database.getReference()
                                .child("posts")
                                .child(postId)
                                .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int commentCount = 0;
                                if (snapshot.exists()){
                                    commentCount = snapshot.getValue(Integer.class);
                                }
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount")
                                        .setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        binding.commentET.setText("");
                                        Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                        //Set Notification data to Notification object
                                        Notification notification = new Notification();
                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notification.setNotificationAt(new Date().getTime());
                                        notification.setPostID(postId);
                                        notification.setPostedBy(postedBy);
                                        notification.setType("comment");

                                        //Save notification data in database
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("notification")
                                                .child(postedBy)
                                                .push()
                                                .setValue(notification);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                //Stop keyboard popping up automatically
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        });


        //Comment Recycler View
        CommentAdapter adapter = new CommentAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(layoutManager);
        binding.commentRv.setAdapter(adapter);
        binding.commentRv.setNestedScrollingEnabled(false);

        //Fetching comment data from database and set in comment recyclerview
        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                binding.scrollView.fullScroll(binding.scrollView.FOCUS_DOWN);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//End of comment Recycler View
    }

    //Close activity on back button press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}