package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemLienOutstandingBinding;
import com.codingbhasha.ps.model.LienOutstandingAmt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class LienOutstandingAdapter extends RecyclerView.Adapter<LienOutstandingViewHolder> {
    List<LienOutstandingAmt> lienOutstandingAmtList = new ArrayList<>();

    @NonNull
    @Override
    public LienOutstandingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemLienOutstandingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_lien_outstanding, parent, false);
        return new LienOutstandingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LienOutstandingViewHolder holder, int position) {
        LienOutstandingAmt currentItem = lienOutstandingAmtList.get(position);
        Date date = new Date(currentItem.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textDate.setText(dated);
        holder.binding.setLienOutstandingAmt(currentItem);
    }

    @Override
    public int getItemCount() {
        return lienOutstandingAmtList.size();
    }

    public void setList(List<LienOutstandingAmt> lienOutstandingAmtList) {
        this.lienOutstandingAmtList = lienOutstandingAmtList;

    }
}

class LienOutstandingViewHolder extends RecyclerView.ViewHolder {
    LayoutItemLienOutstandingBinding binding;

    public LienOutstandingViewHolder(LayoutItemLienOutstandingBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
