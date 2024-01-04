package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemIdStatusBinding;
import com.codingbhasha.ps.model.IdStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class IdStatusAdapter extends RecyclerView.Adapter<IdStatusViewHolder> {
    List<IdStatus> idStatusList = new ArrayList<>();

    @NonNull
    @Override
    public IdStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemIdStatusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_id_status, parent, false);
        return new IdStatusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IdStatusViewHolder holder, int position) {
        IdStatus currentItem = idStatusList.get(position);
        Date date = new Date(Long.parseLong(currentItem.getDateOfJoining()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textJoiningDate.setText("DOJ: "+dated);
        holder.binding.setIdStatus(currentItem);
    }

    @Override
    public int getItemCount() {
        return idStatusList.size();
    }

    public void setList(List<IdStatus> idStatusList) {
        this.idStatusList = idStatusList;
        notifyDataSetChanged();
    }
}
@Obfuscate
class IdStatusViewHolder extends RecyclerView.ViewHolder {

    LayoutItemIdStatusBinding binding;
    public IdStatusViewHolder(LayoutItemIdStatusBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
