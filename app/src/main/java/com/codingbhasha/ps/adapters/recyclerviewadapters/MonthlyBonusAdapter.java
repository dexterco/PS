package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemMonthlyBonusBinding;
import com.codingbhasha.ps.databinding.LayoutItemMonthlyIncomeBinding;
import com.codingbhasha.ps.model.MonthlyBonus;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class MonthlyBonusAdapter extends RecyclerView.Adapter<MonthlyBonusViewHolder> {
    List<MonthlyBonus> monthlyBonusList = new ArrayList<>();

    @NonNull
    @Override
    public MonthlyBonusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemMonthlyBonusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_monthly_bonus, parent, false);
        return new MonthlyBonusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyBonusViewHolder holder, int position) {
        MonthlyBonus currentItem = monthlyBonusList.get(position);
        holder.binding.setMonthlyBonus(currentItem);
    }

    @Override
    public int getItemCount() {
        return monthlyBonusList.size();
    }

    public void setList(List<MonthlyBonus> monthlyBonusList) {
        this.monthlyBonusList = monthlyBonusList;
    }
}

class MonthlyBonusViewHolder extends RecyclerView.ViewHolder {
    LayoutItemMonthlyBonusBinding binding;

    public MonthlyBonusViewHolder(LayoutItemMonthlyBonusBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
