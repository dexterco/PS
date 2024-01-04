package com.codingbhasha.ps.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.BankRequestAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentBankRequestsBinding;
import com.codingbhasha.ps.model.BankRequest;
import com.codingbhasha.ps.viewmodel.BankRequestViewModel;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class BankRequestsFragment extends BaseFragment<FragmentBankRequestsBinding> {
    BankRequestAdapter adapter;
    BankRequestViewModel viewModel;
    List<BankRequest> bankRequestList = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bank_requests;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BankRequestAdapter(getActivity());

        viewModel = new ViewModelProvider(getActivity()).get(BankRequestViewModel.class);
        viewModel.getBankReqs().observe(getActivity(), new Observer<List<BankRequest>>() {
            @Override
            public void onChanged(List<BankRequest> bankRequests) {
                bankRequestList = bankRequests;
                adapter.setList(bankRequestList);
                adapter.notifyDataSetChanged();
            }
        });

        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }
}
