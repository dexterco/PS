package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemPayoutSummaryBinding;
import com.codingbhasha.ps.model.PayoutSummary;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class PayoutSummaryAdapter extends RecyclerView.Adapter<PayoutSummaryViewHolder> {
    List<PayoutSummary> payoutSummaryList = new ArrayList<>();

    @NonNull
    @Override
    public PayoutSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemPayoutSummaryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_payout_summary, parent, false);
        return new PayoutSummaryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PayoutSummaryViewHolder holder, int position) {

        PayoutSummary currentItem = payoutSummaryList.get(position);
        Date date = new Date(currentItem.getDate());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textDate.setText(dated);
        holder.binding.textUserid.setVisibility(View.GONE);
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("spurandhar0@gmail.com")){
            holder.binding.textUserid.setVisibility(View.VISIBLE);
        } else {
            holder.binding.textUserid.setVisibility(View.GONE);
        }
        Log.e("asdad",currentItem.getAccNum() + currentItem.getTransactionID());

        holder.binding.setPayoutSummary(currentItem);

    }

    @Override
    public int getItemCount() {
        return payoutSummaryList.size();
    }

    public void setList(List<PayoutSummary> payoutSummaryList) {
        this.payoutSummaryList = payoutSummaryList;
    }
}

class PayoutSummaryViewHolder extends RecyclerView.ViewHolder {
    LayoutItemPayoutSummaryBinding binding;

    public PayoutSummaryViewHolder(LayoutItemPayoutSummaryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
