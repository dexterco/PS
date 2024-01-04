package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemBankApprovedBinding;
import com.codingbhasha.ps.model.BankRequest;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class BankApprovedAdapter extends RecyclerView.Adapter<BankApprovedViewHolder> {
    List<BankRequest> bankList = new ArrayList<>();

    @NonNull
    @Override
    public BankApprovedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemBankApprovedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_bank_approved, parent, false);
        return new BankApprovedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BankApprovedViewHolder holder, int position) {
        BankRequest currentItem = bankList.get(position);
        holder.binding.setBank(currentItem);
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public void setList(List<BankRequest> bankList) {
        this.bankList = bankList;
        notifyDataSetChanged();
    }
}
@Obfuscate
class BankApprovedViewHolder extends RecyclerView.ViewHolder {

    LayoutItemBankApprovedBinding binding;

    public BankApprovedViewHolder(LayoutItemBankApprovedBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
