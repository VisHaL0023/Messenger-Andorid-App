package com.vishal.chitchat.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vishal.chitchat.Activities.AddPost;
import com.vishal.chitchat.Adapters.PostAdapter;
import com.vishal.chitchat.Adapters.UsersAdapter;
import com.vishal.chitchat.Adapters.topStatusAdapter;
import com.vishal.chitchat.Models.Post;
import com.vishal.chitchat.Models.Status;
import com.vishal.chitchat.Models.Story;
import com.vishal.chitchat.Models.UserStatus;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.FragmentStatusViewerBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    //Define your views here
    //We are not using Binding here thats why we need to define our views
    ShimmerRecyclerView dashboardRV,storyRV;
    ArrayList<Story> storyList;
    ArrayList<Post> postList;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    RoundedImageView addStoryImage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialog;
    ConstraintLayout group;
    NestedScrollView scrollView;
    CircleImageView profileImage;
    ImageView notification, addPost;
    FragmentStatusViewerBinding binding;
    ArrayList<Users> users;
    UsersAdapter usersAdapter;
    topStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    Date date = new Date();
    Users user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Initializing views
        scrollView = view.findViewById(R.id.scroll);
        storyRV = view.findViewById(R.id.storyRV);
        dashboardRV = view.findViewById(R.id.dashboardRV);
        dashboardRV.showShimmerAdapter();
        storyRV.showShimmerAdapter();
        group = view.findViewById(R.id.group);
        profileImage = view.findViewById(R.id.profileImage);
        notification = view.findViewById(R.id.notification);
        addPost = view.findViewById(R.id.addPost);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        //Loading dialog
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);


//        //Story Recycler View
//        storyList = new ArrayList<>();
//        StoryAdapter adapter = new StoryAdapter(storyList, getContext());
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
//        storyRV.setLayoutManager(layoutManager);
//        storyRV.setNestedScrollingEnabled(false);


        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), com.vishal.chitchat.Activities.notification.class);
                startActivity(intent);
            }
        });



        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        database = FirebaseDatabase.getInstance();

        database.getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(Users.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        usersAdapter = new UsersAdapter(getContext(), users);
        statusAdapter = new topStatusAdapter(getContext(), userStatuses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRV.setLayoutManager(layoutManager);
        storyRV.setAdapter(statusAdapter);
        storyRV.setNestedScrollingEnabled(false);

        storyRV.showShimmerAdapter();


        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
//                        if(storySnapshot.hasChild("statuses")) {
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();
                        long time = status.getLastUpdated();

                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            if (statusSnapshot.exists()) {
                                Status sampleStatus = statusSnapshot.getValue(Status.class);
                                statuses.add(sampleStatus);



                                if (date.getTime() > time) {
                                    statusSnapshot.child("statuses").getRef().removeValue();
                                }
                            } else {
                                Toast.makeText(getContext(), "No status found", Toast.LENGTH_SHORT).show();
                            }

                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                }
                storyRV.hideShimmerAdapter();
                statusAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        database.getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    //Set user profile using Picasso
                    Picasso.get()
                            .load(user.getProfileImage())
                            .placeholder(R.drawable.placeholder)
                            .into(profileImage);
                    //Set user name and profession

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPost.class);
                startActivity(intent);
            }
        });


        //Fetching Story data from database and set in story recyclerview
//        database.getReference()
//                .child("stories").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    storyList.clear();
//                    for (DataSnapshot storySnapshot :snapshot.getChildren()){
//                        Story story = new Story();
//                        story.setStoryBy(storySnapshot.getKey());
//                        story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));
//
//                        ArrayList<UserStories> stories = new ArrayList<>();
//                        for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
//                            UserStories userStories = snapshot1.getValue(UserStories.class);
//                            stories.add(userStories);
//                        }
//                        story.setStories(stories);
//                        storyList.add(story);
//                    }
//                    storyRV.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                    //Hide shimmer adapter when data load
//                    storyRV.hideShimmerAdapter();
//                    group.setVisibility(View.VISIBLE);
//                }
//                else{
//                    Toast.makeText(getContext(), "No story", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//            });//End of story Recycler View


        //Post Recycler View
        postList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        dashboardRV.setLayoutManager(linearLayoutManager);
        dashboardRV.addItemDecoration(new DividerItemDecoration(dashboardRV.getContext(), DividerItemDecoration.VERTICAL));
        dashboardRV.setNestedScrollingEnabled(false);

        //Fetching Post data from database and set in post recyclerview
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    postList.add(post);
                }
                dashboardRV.setAdapter(postAdapter);
                dashboardRV.hideShimmerAdapter();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//End of post Recycler View


        //Upload Story
        addStoryImage = view.findViewById(R.id.storyImg);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 75);
            }
        });

//        //Open gallery for images
//        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent()
//                , new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//                        addStoryImage.setImageURI(result);
//
//                        //Show loading dialog as user select image
//                        dialog.show();
//                        //Define storage reference for story image in Firebase Storage
//                        final StorageReference reference = storage.getReference()
//                                .child("stories")
//                                .child(FirebaseAuth.getInstance().getUid())
//                                .child(new Date().getTime() + "");
//                        //Store image in reference you define above
//                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                //Get image URL from Storage and store a copy of image in Firebase database
//                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        Story story = new Story();
//                                        story.setStoryAt(new Date().getTime());
//
//                                        //Create a stories child in database
//                                        database.getReference()
//                                                .child("stories")
//                                                .child(FirebaseAuth.getInstance().getUid())
//                                                .child("postedBy")
//                                                .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                //Set story image and time
//                                                UserStories stories = new UserStories(uri.toString(), story.getStoryAt());
//
//                                                //Save story data in database
//                                                database.getReference()
//                                                        .child("stories")
//                                                        .child(FirebaseAuth.getInstance().getUid())
//                                                        .child("userStories")
//                                                        .push()
//                                                        .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void unused) {
//                                                        dialog.dismiss();
//                                                    }
//                                                });
//                                            }
//                                        });
//                                    }
//                                });
//                            }
//                        });
//
//                    }
//                });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (data.getData() != null) {
                dialog.show();
                String url = data.getData().toString();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(Uri.parse(url)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        dialog.setMessage((int) p + "% Uploading...");
                    }
                });
            }
        }
        else{
            Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

}