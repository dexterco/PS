package com.codingbhasha.ps.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.FundsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentFundsBinding;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class FundsFragment extends BaseFragment<FragmentFundsBinding> {
    FundsAdapter adapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_funds;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new FundsAdapter(getChildFragmentManager());
        adapter.addFragment(new PayoutRequestFragment(getActivity()), "Payout Request");
        adapter.addFragment(new PayoutSummaryFragment(), "Payout Summary");

        dataBinding.viewPager.setCurrentItem(0);
        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        dataBinding.viewPager.setCurrentItem(0);
    }
}
