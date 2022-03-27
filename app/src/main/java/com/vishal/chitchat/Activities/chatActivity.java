package com.vishal.chitchat.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vishal.chitchat.Adapters.MessagesAdapter;
import com.vishal.chitchat.Models.AES;
import com.vishal.chitchat.Models.Message;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.Services.TimeAgo;
import com.vishal.chitchat.databinding.ActivityChatBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class chatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;
    String senderUid, receiverUid;
    FirebaseDatabase database;
    //for photos send to chat
    FirebaseStorage storage;
    ProgressDialog dialog;
    ValueEventListener seenListener;
    DatabaseReference reference;
    FirebaseUser fuser;
    int isBlocked ;

    String statusText, profilePic,name,phoneNumber,emailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();



        //getIntent from UserAdapter
         name = getIntent().getStringExtra("name");
        // For getting the Profile Image for toolBar
         profilePic = getIntent().getStringExtra("image");
         emailId = getIntent().getStringExtra("email");
         phoneNumber = getIntent().getStringExtra("phone");
         statusText = getIntent().getStringExtra("statusText");


        //for photos send to chat
        storage = FirebaseStorage.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image");
        dialog.setCancelable(false);

        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        if (MessagesAdapter.fullScreen){
            binding.fullImageView.setVisibility(View.VISIBLE);
            Bundle extras = getIntent().getExtras();
            Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
            binding.fullImageView.setImageBitmap(bmp);
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String blockedId = receiverUid + auth.getCurrentUser().getUid();
        firebaseDatabase.getReference().child("blockedUsers")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (snapshot.hasChild(blockedId)){
                                String value = snapshot.child(blockedId).getValue().toString();
                                if (value.equals("1")){
                                    isBlocked = 1;
                                    Toast.makeText(chatActivity.this, "You are blocked by user", Toast.LENGTH_SHORT).show();
                                    binding.sendBtn.setVisibility(View.GONE);
                                    binding.cardView.setVisibility(View.GONE);
                                    binding.blockLayout.setVisibility(View.VISIBLE);
                                    binding.blockText.setText("You can't send Message because you are blocked by "+name);
                                }
                                else{
                                    isBlocked=0;
                                    binding.sendBtn.setVisibility(View.VISIBLE);
                                    binding.cardView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        database.getReference().child("presence").child(receiverUid).child(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String checker = snapshot.getValue().toString();
                            if (checker.equals("1")){
                                binding.status.setVisibility(View.VISIBLE);
                                binding.status.setText("typing...");
                            }
                            else{
                                database.getReference().child("presence").child(receiverUid).child("privacy")
                                        .addValueEventListener(new ValueEventListener() {
                                            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.hasChild("onlineToast")){
                                                        String checker = snapshot.child("onlineToast").getValue().toString();
                                                        if(checker.equals("online")) {
                                                            binding.status.setVisibility(View.VISIBLE);
                                                            binding.status.setText("Online");
                                                        }
                                                        else if(snapshot.hasChild("lastSeenPrivacy")){
                                                            String seen = snapshot.child("lastSeenPrivacy").getValue().toString();
                                                            if(seen.equals("Nobody")){
                                                                binding.status.setVisibility(View.GONE);
                                                            }
                                                            if(seen.equals("EveryOne")){
                                                                binding.status.setVisibility(View.VISIBLE);
                                                                binding.status.setText("last Seen "+ TimeAgo.getFormattedLastSeen(Long.parseLong(checker)));
                                                            }
                                                        }
                                                        else{
                                                            binding.status.setVisibility(View.VISIBLE);
                                                            binding.status.setText("last Seen "+ TimeAgo.getFormattedLastSeen(Long.parseLong(checker)));
                                                        }
                                                    }


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //to move automatically the recycler view to end of message list
        linearLayoutManager.setStackFromEnd(true);

        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.recyclerView.setAdapter(adapter);

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(chatActivity.this, imageViewer.class);
                intent.putExtra("userName", name);
                intent.putExtra("profileImage", profilePic);
                startActivity(intent);
            }
        });

        //setting the Name, Image to toolbar
        binding.userName.setText(name);
        Glide.with(chatActivity.this).load(profilePic).placeholder(R.drawable.avatar).into(binding.profile);

        //for retrieving the messages from FirebaseDatabase

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        binding.recyclerView.scrollToPosition(binding.recyclerView.getAdapter().getItemCount() - 1);
                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.messageBox.getText().toString().trim().isEmpty()) {
                    binding.msgContainer.setVisibility(View.VISIBLE);
                    Toast.makeText(chatActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
                else if(isBlocked ==1){
                    binding.msgContainer.setVisibility(View.GONE);
                    Toast.makeText(chatActivity.this, "Blocked user", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.msgContainer.setVisibility(View.VISIBLE);
                    String msg = binding.messageBox.getText().toString().trim();
                    AES aes = new AES(getApplicationContext());
                    String messageTxt =aes.Encrypt(msg, getApplicationContext());
                    Date date = new Date();
                    Message message = new Message(messageTxt, senderUid, date.getTime(), receiverUid, false);

                    //to Empty the box After Message Sent
                    binding.messageBox.setText("");

                    //message id of send or received msg (one which is shown on sender side and other same msg on receiver side )must have same id in the database
                    //so that we can easily set feeling on one and it get reflected on other side (lets create a common random key )
                    String randomKey = database.getReference().push().getKey();

                    //For Storing The Last Msg and Time
                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", message.getMessage());
                    lastMsgObj.put("lastMsgTime", date.getTime());

                    //uploading last message to database
                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                    //Adding the Messages to Firebase one for receiver and other for sender
                    database.getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(randomKey)
                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    });

                }

            }
        });


        binding.menuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(chatActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.topmenu, popup.getMenu());
                Menu menuOpts = popup.getMenu();

                if(isBlocked == 0) {
                    menuOpts.getItem(1).setTitle("Unblock User");
                }else{
                    menuOpts.getItem(1).setTitle("Block User");
                }


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){

                            case R.id.imageInfo:

                                Intent intent = new Intent(chatActivity.this, userProfile.class);
                                intent.putExtra("name", name);
                                intent.putExtra("profilePic", profilePic);
                                intent.putExtra("emailId", emailId);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("statusText", statusText);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

                                return true;

                            case R.id.block:

                                    new AlertDialog.Builder(chatActivity.this)
                                            .setTitle("Block User")
                                            .setMessage("Do you really want to Block "+name+" ?")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                    String blockedId = auth.getCurrentUser().getUid() + receiverUid;
                                                    firebaseDatabase.getReference().child("blockedUsers")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()){
                                                                        if (snapshot.hasChild(blockedId)) {
                                                                            String value = snapshot.child(blockedId).getValue().toString();
                                                                            if (value.equals("1")){
                                                                                firebaseDatabase.getReference().child("blockedUsers").child(blockedId)
                                                                                        .setValue("0");
                                                                                isBlocked = 0;
                                                                                Toast.makeText(chatActivity.this, name+" Unblocked", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else{
                                                                                firebaseDatabase.getReference().child("blockedUsers").child(blockedId)
                                                                                        .setValue("1");
                                                                                isBlocked = 1;
                                                                                Toast.makeText(chatActivity.this, name+" Blocked", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                        }
                                                                        firebaseDatabase.getReference().child("blockedUsers").child(blockedId)
                                                                                .setValue("1");
                                                                        isBlocked = 1;
                                                                        Toast.makeText(chatActivity.this, name+" Blocked", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                }
                                            }).setNegativeButton("No", null).show();



                                return true;

                            case R.id.invite:
                                Toast.makeText(chatActivity.this, "Invitation", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.search:
                                Toast.makeText(chatActivity.this, "Search", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.deleteChat:



                                return true;


                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


        //BAck Navigation Arrow
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            }
        });


        //  For sending The Images to Chat
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 256);
                intent.putExtra("outputY", 256);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, 25);
            }
        });



        //for typing indication
        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).child(receiverUid).setValue("1");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }

            final Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).child("privacy").child("onlineToast").setValue("online");
                    database.getReference().child("presence").child(senderUid).child(receiverUid).setValue("0");
                }
            };
        });

        seenMessage(receiverUid);

    }



    private void seenMessage(String userID) {
        //Toast.makeText(chatActivity.this, "SeenMessageCalled", Toast.LENGTH_SHORT).show();
        reference = FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .child("messages");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    Message message = snapshot2.getValue(Message.class);
                    if (Objects.requireNonNull(message).getReceiverId().equals(fuser.getUid()) && message.getSenderId().equals(userID)) {
                        // Toast.makeText(chatActivity.this, "True", Toast.LENGTH_SHORT).show();
                        message.setSeen(true);
                        database.getReference()
                                .child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(snapshot2.getKey()))
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(snapshot2.getKey())
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //For showing Online and Storing in Database
    @Override
    protected void onResume() {
        super.onResume();
        //Updating Data on Online Offline in Database
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(Objects.requireNonNull(currentId)).child("privacy").child("onlineToast").setValue("online");
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



    //for image sent in Chats
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {

                    String url = data.getData().toString();                    //Calender Is Used here for getting unique Id as time in milisecond
                    //will be unique
                    Calendar calendar = Calendar.getInstance();
                    // Creating Storage Reference
                    StorageReference reference = storage.getReference()
                            .child("chats")
                            .child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    // uploading the file
                    reference.putFile(Uri.parse(url)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //it is the filePath of sent Image on chat
                                        String filePath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();
                                        Date date = new Date();
                                        Message message = new Message(messageTxt, senderUid, date.getTime(), receiverUid, false);

                                        //setting the Image path to Message Molder class or obj so that it can get updated in UI
                                        message.setImageUrl(filePath);
                                        message.setMessage("Photo");
                                        //to Empty the box After Message Sent
                                        binding.messageBox.setText("");

                                        //message id of send or received msg (one which is shown on sender side and other same msg on receiver side )must have same id in the database
                                        //so that we can easily set feeling on one and it get reflected on other side (lets create a common random key )
                                        String randomKey = database.getReference().push().getKey();

                                        //For Storing The Last Msg and Time
                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference()
                                                .child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference()
                                                        .child("chats")
                                                        .child(receiverRoom)
                                                        .child("messages")
                                                        .child(randomKey)
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                            }
                                        });


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
        }
    }



}