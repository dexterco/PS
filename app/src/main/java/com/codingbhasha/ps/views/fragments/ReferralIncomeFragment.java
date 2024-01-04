package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.codingbhasha.ps.adapters.recyclerviewadapters.ReferrerIncomeAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentReferralIncomeBinding;
import com.codingbhasha.ps.model.ReferralIncome;
import com.codingbhasha.ps.viewmodel.ReferralIncomeViewModel;
import com.codingbhasha.ps.views.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class ReferralIncomeFragment extends BaseFragment<FragmentReferralIncomeBinding>   {
    MainActivity mainActivity;
    ReferralIncomeViewModel viewModel;
    List<ReferralIncome> referralIncomeList = new ArrayList<>();
    ReferrerIncomeAdapter adapter;
    String plan = "";
    ProgressDialog progressDialog;

    public ReferralIncomeFragment(Context context) {
        mainActivity = (MainActivity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_referral_income;
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


        viewModel = new ViewModelProvider(getActivity()).get(ReferralIncomeViewModel.class);

        adapter = new ReferrerIncomeAdapter();
        dataBinding.setIsDataAvailable(false);
        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }

    private void getReferralIncomes() {
        progressDialog.show();
        viewModel.getReferralIncomes(plan).observe(getActivity(), new Observer<List<ReferralIncome>>() {
            @Override
            public void onChanged(List<ReferralIncome> referralIncomes) {
                referralIncomeList = referralIncomes;
                if (referralIncomeList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(referralIncomeList);
                    adapter.notifyDataSetChanged();
                } else
                    dataBinding.setIsDataAvailable(false);
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        getReferralIncomes();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getReferralIncomes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
