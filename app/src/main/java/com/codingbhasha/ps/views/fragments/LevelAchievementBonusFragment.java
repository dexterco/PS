package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.codingbhasha.ps.adapters.recyclerviewadapters.LevelAchievementBonusAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentLevelAchievementBonusBinding;
import com.codingbhasha.ps.model.LevelAchievementBonus;
import com.codingbhasha.ps.viewmodel.LevelAchievementBonusViewModel;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class LevelAchievementBonusFragment extends BaseFragment<FragmentLevelAchievementBonusBinding>  {
    MainActivity mainActivity;
    String plan;
    CollectionReference levelAchievementRef;
    DocumentReference userDoc;
    LevelAchievementBonusViewModel viewModel;
    List<LevelAchievementBonus> levelAchievementBonusList = new ArrayList<>();
    LevelAchievementBonusAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public LevelAchievementBonusFragment(Context context) {
        mainActivity = (MainActivity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_level_achievement_bonus;
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

        preferences = mainActivity.getSharedPreferences("labPref", Context.MODE_PRIVATE);
        editor = preferences.edit();

        adapter = new LevelAchievementBonusAdapter();
        dataBinding.setIsDataAvailable(false);

        viewModel = new ViewModelProvider(getActivity()).get(LevelAchievementBonusViewModel.class);

        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }

    private void setup() {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();

        viewModel.getLevelAchievementBonus(plan).observe(getActivity(), new Observer<List<LevelAchievementBonus>>() {
            @Override
            public void onChanged(List<LevelAchievementBonus> levelAchievementBonuses) {
                levelAchievementBonusList = levelAchievementBonuses;
                if (levelAchievementBonusList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(levelAchievementBonusList);
                    adapter.notifyDataSetChanged();
                } else
                    dataBinding.setIsDataAvailable(false);
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
        setup();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                setup();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
