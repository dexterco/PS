package com.codingbhasha.ps.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.AdminSettingsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminSettingsBinding;
import com.codingbhasha.ps.views.activities.AdminActivity;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AdminSettingsFragment extends BaseFragment<FragmentAdminSettingsBinding> {
    AdminSettingsAdapter adapter;
    AdminActivity mainActivity;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_settings;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.spinnerPlan.setVisibility(View.GONE);
        mainActivity.dataBinding.toolbarId.setVisibility(View.GONE);
        mainActivity.dataBinding.toolbarValidity.setVisibility(View.GONE);
        adapter = new AdminSettingsAdapter(getChildFragmentManager());
        adapter.addFragment(new BankRequestsFragment(), "Bank Requests");
        adapter.addFragment(new BankApprovedFragment(), "Banks Approved");
        adapter.addFragment(new UserProfileFragment(), "User Profiles");

        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (AdminActivity) context;
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
