package com.codingbhasha.ps.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.EarningsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentLoanBinding;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class LoanFragment extends BaseFragment<FragmentLoanBinding> {
    EarningsAdapter adapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_loan;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new EarningsAdapter(getChildFragmentManager());
        adapter.addFragment(new ApplyLoanFragment(), "Apply AI");
        adapter.addFragment(new LienOutstandingFragment(), "AI History");
        adapter.addFragment(new LoanAccountFragment(), "AI Account");
        dataBinding.viewPager.setOffscreenPageLimit(1);
        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager, true);
    }
}
