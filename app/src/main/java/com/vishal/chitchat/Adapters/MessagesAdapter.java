package com.vishal.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.vishal.chitchat.Activities.imageViewer;
import com.vishal.chitchat.Models.AES;
import com.vishal.chitchat.Models.Message;
import com.vishal.chitchat.Models.Users;
import com.vishal.chitchat.R;
import com.vishal.chitchat.databinding.DeleteDialogBinding;
import com.vishal.chitchat.databinding.ItemReceivedBinding;
import com.vishal.chitchat.databinding.ItemSentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


//when more than one ViewHolder are present we don't need to extend with any ViewHolder just extent using RecyclerView
public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;
    FirebaseRemoteConfig remoteConfig;


    public static boolean fullScreen;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    public static final int IMAGE_TYPE_LEFT = 4;
    public static final int IMAGE_TYPE_RIGHT = 5;

    String senderRoom;
    String receiverRoom;

    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    //ViewHolder For Recycler View
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false);
            return new ReceivedViewHolder(view);
        }

    }

    //for Getting whether the Message is Sent or received
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //if current LoggedIn userId is Same as Sender Id
        if (Objects.equals(FirebaseAuth.getInstance().getUid(), message.getSenderId())) {
            if(messages.get(position).equals("Photo")){
                return IMAGE_TYPE_RIGHT;
            }
            else{
                return ITEM_SENT;
            }

        } else {
            if (messages.get(position).equals("Photo")){

                return IMAGE_TYPE_LEFT;
            }
            else{
                return ITEM_RECEIVE;
            }

        }

//        if (Objects.equals(FirebaseAuth.getInstance().getUid(), message.getSenderId())) {
//            return ITEM_SENT;
//        } else {
//            return ITEM_RECEIVE;
//        }

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        //Feeling Or Reaction On Message
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        //Building Pop Up
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            //When ANy Reaction Clicked Set The Image Resource on Text
            if (holder.getClass() == SentViewHolder.class) {
                SentViewHolder viewHolder = (SentViewHolder) holder;
                if (pos >= 0) {
                    viewHolder.binding.feelingSent.setImageResource(reactions[pos]);
                    viewHolder.binding.feelingSent.setVisibility(View.VISIBLE);
                }

            } else {
                ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
                if (pos >= 0) {
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }

            }
//update the value of feeling and then Upload it to the Database
            message.setFeeling(pos);

            //updating feelings to Database(sender)
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            // (receiver)
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);


            return true; // true is closing popup, false is requesting a new selection


        });
        //Setting the Message to View
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            AES aes = new AES(context);
            String decryptMsg = aes.Decrypt(message.getMessage(),context);
            viewHolder.binding.msgSent.setText(decryptMsg);
            long time = message.getTimeStamp();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            viewHolder.binding.msgTime.setText(dateFormat.format(new Date(time)));

            //for message seen or not
//**************************************************************************************************
            if (position == messages.size() - 1) {
                if (message.isSeen()) {
                    viewHolder.binding.seenTextView.setVisibility(View.VISIBLE);
                    viewHolder.binding.seenTextView.setText("Seen");
                } else {
                    viewHolder.binding.seenTextView.setVisibility(View.VISIBLE);
                    viewHolder.binding.seenTextView.setText("Sent");
                }
            } else {
                viewHolder.binding.seenTextView.setVisibility(View.GONE);

            }


//**************************************************************************************************
            //Setting the Image sent to UI
            if (message.getMessage().equals("Photo")) {
                //we will make the test message gone
                viewHolder.binding.imageContainer.setVisibility(View.VISIBLE);
                viewHolder.binding.linearLayout2.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.img_placeholder)
                        .into(viewHolder.binding.imageSent);

                viewHolder.binding.imageSent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Users users = new Users();
                        String profileImage = message.getImageUrl();
                        Intent intent = new Intent(context, imageViewer.class);
                        intent.putExtra("userName", users.getName());
                        intent.putExtra("profileImage", profileImage);
                        context.startActivity(intent);
                    }
                });

            }

            //updating the UI of the chat Using Adapter
            if (message.getFeeling() >= 0) {
                viewHolder.binding.feelingSent.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feelingSent.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feelingSent.setVisibility(View.GONE);
            }
            //On Sent Msg Touched Popup



            viewHolder.binding.msgSent.setOnTouchListener(new View.OnTouchListener() {
                double firsttouch=0;
                boolean isup=false;
                int millistotouch=1000;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                        firsttouch = (double) System.currentTimeMillis();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isup) {
                                    //
                                    // Do your long click work
                                    popup.onTouch(view,motionEvent);
                                    //
                                }
                                else
                                {
                                    firsttouch=0;
                                    isup=false;
                                }
                            }
                    }, millistotouch);
                    return true;
                }
            if (motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    if (((double) System.currentTimeMillis()-firsttouch)<millistotouch)
                    {
                        isup=true;
                        //
                        // Do your short click work
                        //
                    }

                }
            return false;
            }
        });



//            On ImageSent FeelingPopup
//            viewHolder.binding.imageSent.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v, event);
//                    return false;
//                }
//            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();

                    if(remoteConfig.getBoolean("isEveryoneDeletionEnabled")) {
                        binding.everyone.setVisibility(View.GONE);
                    } else {
                        binding.everyone.setVisibility(View.VISIBLE);
                    }
                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage("This message is removed.");
                            message.setFeeling(-1);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(message);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(message);
                            dialog.dismiss();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });

        } else {
            ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
            AES aes = new AES(context);
            String decryptMsg = aes.Decrypt(message.getMessage(), context);
            viewHolder.binding.message.setText(decryptMsg);
            long time = message.getTimeStamp();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            viewHolder.binding.msgRTime.setText(dateFormat.format(new Date(time)));

//            Receiver side showing the sent Image
            if (message.getMessage().equals("Photo")) {
                //we will make the test message gone
                viewHolder.binding.imageContainer.setVisibility(View.VISIBLE);
                viewHolder.binding.linearLayout2.setVisibility(View.INVISIBLE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.img_placeholder)
                        .into(viewHolder.binding.imageRecived);

                viewHolder.binding.imageRecived.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Users users = new Users();
                        String profileImage = message.getImageUrl();
                        Intent intent = new Intent(context, imageViewer.class);
                        intent.putExtra("userName", users.getName());
                        intent.putExtra("profileImage", profileImage);
                        context.startActivity(intent);
                    }
                });

            }

            //Updating The UI of chat using Adapter
            if (message.getFeeling() >= 0) {
                //message.setFeeling(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            //On Received Msg Touch Popup
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
            //On ImageReceived Feeling Popup
//            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v, event);
//                    return false;
//                }
//            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();
                    binding.everyone.setVisibility(View.GONE);

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentViewHolder extends RecyclerView.ViewHolder {
        ItemSentBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);

        }
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        ItemReceivedBinding binding;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceivedBinding.bind(itemView);
        }
    }

}
