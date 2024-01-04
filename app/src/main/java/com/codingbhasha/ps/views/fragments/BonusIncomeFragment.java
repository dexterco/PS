package com.codingbhasha.ps.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.viewpageradapters.BonusIncomeAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentBonusIncomeBinding;
import com.codingbhasha.ps.views.activities.MainActivity;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class BonusIncomeFragment extends BaseFragment<FragmentBonusIncomeBinding> {
    BonusIncomeAdapter adapter;
    MainActivity mainActivity;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public String plan="";

    public BonusIncomeFragment(Context context) {
        mainActivity = (MainActivity) context;
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bonus_income;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BonusIncomeAdapter(getChildFragmentManager());
        adapter.addFragment(new MonthlyBonusFragment(mainActivity), "Monthly Bonus");
        adapter.addFragment(new LevelAchievementBonusFragment(mainActivity), "Level Achievement Bonus");



        preferences = mainActivity.getSharedPreferences("bonusIncomePref", Context.MODE_PRIVATE);
        editor = preferences.edit();
        dataBinding.viewPager.setAdapter(adapter);
        dataBinding.tabLayout.setupWithViewPager(dataBinding.viewPager);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
