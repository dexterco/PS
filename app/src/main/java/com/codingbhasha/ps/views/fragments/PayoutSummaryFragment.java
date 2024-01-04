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
import com.codingbhasha.ps.adapters.recyclerviewadapters.PayoutSummaryAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentPayoutSummaryBinding;
import com.codingbhasha.ps.model.PayoutSummary;
import com.codingbhasha.ps.viewmodel.PayoutSummaryViewModel;
import com.codingbhasha.ps.views.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class PayoutSummaryFragment extends BaseFragment<FragmentPayoutSummaryBinding>  {
    MainActivity mainActivity;
    List<PayoutSummary> payoutSummaryList = new ArrayList<>();
    PayoutSummaryAdapter adapter;
    PayoutSummaryViewModel viewModel;
    String plan;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_payout_summary;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();

        adapter = new PayoutSummaryAdapter();
        dataBinding.setIsDataAvailable(false);

        viewModel = new ViewModelProvider(getActivity()).get(PayoutSummaryViewModel.class);
        viewModel.context = getActivity();



        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }

    private void getPayoutSummaries() {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();
        viewModel.getPayoutSummary(plan).observe(getActivity(), new Observer<List<PayoutSummary>>() {
            @Override
            public void onChanged(List<PayoutSummary> payoutSummaries) {
                payoutSummaryList = payoutSummaries;
                if (payoutSummaryList.size() > 0) {

                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(payoutSummaryList);
                    adapter.notifyDataSetChanged();
                } else {
                    dataBinding.setIsDataAvailable(false);
                }
            }
        });
        progressDialog.dismiss();
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
        getPayoutSummaries();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getPayoutSummaries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
