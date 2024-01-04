package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemLevelAchievementBonusBinding;
import com.codingbhasha.ps.model.LevelAchievementBonus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class LevelAchievementBonusAdapter extends RecyclerView.Adapter<LevelAchievementBonusViewHolder> {
    List<LevelAchievementBonus> levelAchievementBonusList = new ArrayList<>();

    @NonNull
    @Override
    public LevelAchievementBonusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemLevelAchievementBonusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_level_achievement_bonus, parent, false);
        return new LevelAchievementBonusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelAchievementBonusViewHolder holder, int position) {
        LevelAchievementBonus currentItem = levelAchievementBonusList.get(position);
        Date date = new Date(currentItem.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textDate.setText(dated);
        holder.binding.setLevelAchievement(currentItem);
    }

    @Override
    public int getItemCount() {
        return levelAchievementBonusList.size();
    }

    public void setList(List<LevelAchievementBonus> levelAchievementBonusList) {
        this.levelAchievementBonusList = levelAchievementBonusList;
    }
}
@Obfuscate
class LevelAchievementBonusViewHolder extends RecyclerView.ViewHolder {
    LayoutItemLevelAchievementBonusBinding binding;

    public LevelAchievementBonusViewHolder(LayoutItemLevelAchievementBonusBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
