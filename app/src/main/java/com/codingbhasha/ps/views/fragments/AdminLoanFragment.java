package com.codingbhasha.ps.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.LoanAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminLoanBinding;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AdminLoanFragment extends BaseFragment<FragmentAdminLoanBinding> {
    LoanAdapter adapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_loan;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new LoanAdapter(getChildFragmentManager());
        adapter.addFragment(new LoanRequestsFragment(), "Advance Income Requests");

        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
