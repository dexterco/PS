package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemWalletTransactionBinding;
import com.codingbhasha.ps.model.WalletTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionViewHolder> {
    List<WalletTransaction> walletTransactionList = new ArrayList<>();

    @NonNull
    @Override
    public WalletTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemWalletTransactionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_wallet_transaction, parent, false);
        return new WalletTransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletTransactionViewHolder holder, int position) {
        WalletTransaction currentItem = walletTransactionList.get(position);
        Date date = new Date(currentItem.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textDate.setText(dated);
        holder.binding.setTransaction(currentItem);
    }

    @Override
    public int getItemCount() {
        return walletTransactionList.size();
    }

    public void setList(List<WalletTransaction> walletTransactionList) {
        this.walletTransactionList = walletTransactionList;
        notifyDataSetChanged();
    }
}

class WalletTransactionViewHolder extends RecyclerView.ViewHolder {

    LayoutItemWalletTransactionBinding binding;

    public WalletTransactionViewHolder(LayoutItemWalletTransactionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
