package com.vishal.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vishal.chitchat.Activities.MainActivity;
import com.vishal.chitchat.Models.Status;
import com.vishal.chitchat.Models.UserStatus;
import com.vishal.chitchat.R;
import com.vishal.chitchat.Services.TimeAgo;
import com.vishal.chitchat.databinding.ItemStatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class topStatusAdapter extends RecyclerView.Adapter<topStatusAdapter.topStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public topStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public topStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new topStatusViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull topStatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

        //load last status to The CircleStatus view
        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);
        Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.storyImg);
       //Set the No. of status to the circle ring
        holder.binding.statusCircle.setPortionsCount(userStatus.getStatuses().size());
        holder.binding.name.setText(userStatus.getName());
        Glide.with(context).load(userStatus.getProfileImage()).into(holder.binding.profileImage);
        long time = userStatus.getLastUpdated();
//        holder.binding.statusTime.setText("Updated "+ TimeAgo.getFormattedLastSeen(time));

        holder.binding.storyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is from Story Library (MyStory class)
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }

                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        })// Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class topStatusViewHolder extends RecyclerView.ViewHolder {
        ItemStatusBinding binding;

        public topStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
