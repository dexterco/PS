package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemUserProfileBinding;
import com.codingbhasha.ps.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {
    List<UserProfile> userProfileList = new ArrayList<>();

    public interface userProfileClickListener{
        void onClickUserProfile(UserProfile userProfile, int pos);
    }
    UserProfileAdapter.userProfileClickListener listener;

    public UserProfileAdapter(UserProfileAdapter.userProfileClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemUserProfileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_user_profile, parent, false);
        return new UserProfileViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        UserProfile currentItem = userProfileList.get(position);
        holder.binding.setUser(currentItem);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickUserProfile(currentItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userProfileList.size();
    }

    public void setList(List<UserProfile> userProfileList){
        this.userProfileList = userProfileList;
        notifyDataSetChanged();
    }
}

class UserProfileViewHolder extends RecyclerView.ViewHolder {

    LayoutItemUserProfileBinding binding;

    public UserProfileViewHolder(LayoutItemUserProfileBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
