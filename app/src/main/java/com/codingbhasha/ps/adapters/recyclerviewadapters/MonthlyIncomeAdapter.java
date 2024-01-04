package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemMonthlyIncomeBinding;
import com.codingbhasha.ps.model.MonthlyIncome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class MonthlyIncomeAdapter extends RecyclerView.Adapter<MonthlyIncomeViewHolder> {
    List<MonthlyIncome> monthlyIncomeList = new ArrayList<>();
    Context context;
String PlanID;
    public MonthlyIncomeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MonthlyIncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemMonthlyIncomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_monthly_income, parent, false);
        return new MonthlyIncomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyIncomeViewHolder holder, int position) {
        MonthlyIncome currentItem = monthlyIncomeList.get(position);
        String plan = PlanID;
        //Display details according to plan
        if (plan.equals("PlanA")) {
            holder.binding.textL1Multiply.setText("Level 1: 16 x ");
            holder.binding.textL2Multiply.setText("Level 2: 08 x ");
            holder.binding.textL3Multiply.setText("Level 3: 04 x ");
            holder.binding.textL4Multiply.setText("Level 4: 02 x ");
        } else if (plan.equals("PlanB")) {
            holder.binding.textL1Multiply.setText("Level 1: 40 x ");
            holder.binding.textL2Multiply.setText("Level 2: 20 x ");
            holder.binding.textL3Multiply.setText("Level 3: 10 x ");
            holder.binding.textL4Multiply.setText("Level 4: 05 x ");
        } else if (plan.equals("PlanC")) {
            holder.binding.textL1Multiply.setText("Level 1: 80 x ");
            holder.binding.textL2Multiply.setText("Level 2: 40 x ");
            holder.binding.textL3Multiply.setText("Level 3: 20 x ");
            holder.binding.textL4Multiply.setText("Level 4: 10 x ");
        }
        Date date = new Date(currentItem.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dated = dateFormat.format(date);
        holder.binding.textDate.setText(dated);
        holder.binding.setMonthlyIncome(currentItem);
    }

    @Override
    public int getItemCount() {
        return monthlyIncomeList.size();
    }

    public void setList(List<MonthlyIncome> monthlyIncomeList, String planID) {
        this.monthlyIncomeList = monthlyIncomeList;
        PlanID= planID;
    }
}

class MonthlyIncomeViewHolder extends RecyclerView.ViewHolder {
    LayoutItemMonthlyIncomeBinding binding;

    public MonthlyIncomeViewHolder(LayoutItemMonthlyIncomeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
