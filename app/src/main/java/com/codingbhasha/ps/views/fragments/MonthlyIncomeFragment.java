package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.MonthlyIncomeAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentMonthlyIncomeBinding;
import com.codingbhasha.ps.model.MonthlyIncome;
import com.codingbhasha.ps.viewmodel.MonthlyIncomeViewModel;
import com.codingbhasha.ps.views.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class MonthlyIncomeFragment extends BaseFragment<FragmentMonthlyIncomeBinding>  {
    MainActivity mainActivity;
    String plan;

    MonthlyIncomeViewModel viewModel;
    List<MonthlyIncome> monthlyIncomeList = new ArrayList<>();
    MonthlyIncomeAdapter adapter;
    SharedPreferences.Editor editor;


    public MonthlyIncomeFragment(Context context) {
        mainActivity = (MainActivity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_monthly_income;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarValidity.setVisibility(View.VISIBLE);

        viewModel = new ViewModelProvider(getActivity()).get(MonthlyIncomeViewModel.class);

        adapter = new MonthlyIncomeAdapter(getActivity());
        dataBinding.setIsDataAvailable(false);


    }

    private void setup() {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();

        viewModel.getMonthlyIncomes(plan).observe(getActivity(), new Observer<List<MonthlyIncome>>() {
            @Override
            public void onChanged(List<MonthlyIncome> monthlyIncomes) {
                monthlyIncomeList = monthlyIncomes;
                if (monthlyIncomeList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(monthlyIncomeList, plan);
                    adapter.notifyDataSetChanged();
                } else
                    dataBinding.setIsDataAvailable(false);
            }
        });
        progressDialog.dismiss();

        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        setup();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.dataBinding.spinnerPlan.setSelection(i);
                mainActivity.fillData(plan);
                setup();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
