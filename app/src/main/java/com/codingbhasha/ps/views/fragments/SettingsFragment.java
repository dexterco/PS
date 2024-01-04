package com.codingbhasha.ps.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.SettingsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentEarningsBinding;
import com.codingbhasha.ps.views.activities.MainActivity;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class SettingsFragment extends BaseFragment<FragmentEarningsBinding> {
    SettingsAdapter adapter;
    Context context;
MainActivity mainActivity;
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_earnings;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SettingsAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileFragment(context), "Profile");
        adapter.addFragment(new AddBankFragment(), "Add Bank Account");
        adapter.addFragment(new ChangePasswordFragment(), "Change Password");


        dataBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
if (position==1 || position==2){
    mainActivity.dataBinding.spinnerPlan.setVisibility(View.GONE);
    mainActivity.dataBinding.toolbarId.setVisibility(View.GONE);
    mainActivity.dataBinding.toolbarValidity.setVisibility(View.GONE);
}else {
    mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
    mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
    mainActivity.dataBinding.toolbarValidity.setVisibility(View.VISIBLE);
}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity= (MainActivity) context;
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
