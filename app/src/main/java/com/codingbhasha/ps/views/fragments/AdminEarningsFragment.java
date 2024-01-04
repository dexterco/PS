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
import com.codingbhasha.ps.adapters.viewpageradapters.EarningsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentEarningsBinding;
import com.codingbhasha.ps.views.activities.AdminActivity;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AdminEarningsFragment extends BaseFragment<FragmentEarningsBinding> {
    EarningsAdapter adapter;
    AdminActivity mainActivity;

    public AdminEarningsFragment(Context mainActivity) {
        this.mainActivity = (AdminActivity) mainActivity;
    }

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
        adapter = new EarningsAdapter(getChildFragmentManager());
        adapter.addFragment(new  AdminReferralIncomeFragment(), "Referral Income");
        adapter.addFragment(new AdminMonthlyIncomeFragment(), "Monthly Income");
        adapter.addFragment(new AdminBonusIncomeFragment(), "Bonus Income");

        dataBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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
        mainActivity = (AdminActivity) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        dataBinding.viewPager.setCurrentItem(0);
    }
}
