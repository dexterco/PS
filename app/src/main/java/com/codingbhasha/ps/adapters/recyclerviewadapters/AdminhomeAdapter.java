package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemAdminPayoutRequestBinding;
import com.codingbhasha.ps.databinding.LayoutItemHomeBinding;
import com.codingbhasha.ps.databinding.LayoutItemIdStatusBinding;
import com.codingbhasha.ps.model.AdminHome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AdminhomeAdapter extends RecyclerView.Adapter<AdminHomeViewHolder> {
    List<AdminHome> idStatusList = new ArrayList<>();

    @NonNull
    @Override
    public AdminHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_home, parent, false);
        return new AdminHomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminHomeViewHolder holder, int position) {
        AdminHome currentItem = idStatusList.get(position);
        Date date = new Date(Long.parseLong(currentItem.getDateOfJoining()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
       holder.binding.textDate.setText("Date: "+dated);
        holder.binding.setAdminHome(currentItem);
    }

    @Override
    public int getItemCount() {
        return idStatusList.size();
    }

    public void setList(List<AdminHome> idStatusList) {
        this.idStatusList = idStatusList;
        notifyDataSetChanged();
    }
}
@Obfuscate
class AdminHomeViewHolder extends RecyclerView.ViewHolder {

    LayoutItemHomeBinding binding;
    public AdminHomeViewHolder(LayoutItemHomeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
