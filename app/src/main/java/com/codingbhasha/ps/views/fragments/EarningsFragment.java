package com.codingbhasha.ps.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.EarningsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentEarningsBinding;

import androidx.annotation.Nullable;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class EarningsFragment extends BaseFragment<FragmentEarningsBinding> {
    EarningsAdapter adapter;
    int currentPosition = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_earnings;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new EarningsAdapter(getChildFragmentManager());
        adapter.addFragment(new ReferralIncomeFragment(getActivity()), "Referral Income");
        adapter.addFragment(new MonthlyIncomeFragment(getActivity()), "Monthly Income");
        adapter.addFragment(new BonusIncomeFragment(getActivity()), "Bonus Income");

        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.viewPager.setOffscreenPageLimit(1);

        dataBinding.viewPager.setCurrentItem(0);

        dataBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        dataBinding.viewPager.setCurrentItem(0);
    }
}
