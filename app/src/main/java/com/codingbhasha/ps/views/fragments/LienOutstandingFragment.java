package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.codingbhasha.ps.adapters.recyclerviewadapters.LienOutstandingAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentLienOutstandingBinding;
import com.codingbhasha.ps.model.LienOutstandingAmt;
import com.codingbhasha.ps.viewmodel.LienOutstandingViewModel;
import com.codingbhasha.ps.views.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class LienOutstandingFragment extends BaseFragment<FragmentLienOutstandingBinding>   {
    MainActivity mainActivity;
    String plan;
    List<LienOutstandingAmt> lienOutstandingAmtList;
    LienOutstandingAdapter adapter;
    LienOutstandingViewModel viewModel;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_lien_outstanding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarValidity.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
        adapter = new LienOutstandingAdapter();
        dataBinding.setIsDataAvailable(false);


        viewModel = new ViewModelProvider(getActivity()).get(LienOutstandingViewModel.class);
        viewModel.context = getActivity();



//        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
//                plan = adapterView.getItemAtPosition(i).toString();
//                mainActivity.fillData(plan);
//                getLoans();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }

    private void getLoans() {
        lienOutstandingAmtList = new ArrayList<>();
        adapter.notifyDataSetChanged();
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();
        viewModel.getLoans(plan).observe(getActivity(), new Observer<List<LienOutstandingAmt>>() {
            @Override
            public void onChanged(List<LienOutstandingAmt> lienOutstandingAmts) {
                lienOutstandingAmtList = lienOutstandingAmts;

                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(lienOutstandingAmtList);
                    adapter.notifyDataSetChanged();
                if (lienOutstandingAmtList.size() == 0) {
                    dataBinding.setIsDataAvailable(false);
                }

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

        getLoans();

        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getLoans();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
