package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemReferralIncomeBinding;
import com.codingbhasha.ps.model.ReferralIncome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class ReferrerIncomeAdapter extends RecyclerView.Adapter<ReferrerIncomeViewHolder> {
    List<ReferralIncome> referralIncomeList = new ArrayList<>();
    @NonNull
    @Override
    public ReferrerIncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutItemReferralIncomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_item_referral_income, parent, false);
        return new ReferrerIncomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferrerIncomeViewHolder holder, int position) {
        ReferralIncome currentItem = referralIncomeList.get(position);
        Date date = new Date(currentItem.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textDate.setText(dated);
        holder.binding.setReferralIncome(currentItem);
    }

    @Override
    public int getItemCount() {
        return referralIncomeList.size();
    }

    public void setList(List<ReferralIncome> referralIncomeList) {
        this.referralIncomeList = referralIncomeList;
    }
}

class ReferrerIncomeViewHolder extends RecyclerView.ViewHolder {
    LayoutItemReferralIncomeBinding binding;

    public ReferrerIncomeViewHolder(LayoutItemReferralIncomeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
