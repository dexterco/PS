package com.codingbhasha.ps.views.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.WalletTransactionAdapter;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityReferAndEarnBinding;
import com.codingbhasha.ps.databinding.ActivityWalletTransactionsBinding;
import com.codingbhasha.ps.model.WalletTransaction;
import com.codingbhasha.ps.viewmodel.WalletTransactionViewModel;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class WalletTransactionActivity extends BaseActivity<ActivityWalletTransactionsBinding> {
    WalletTransactionAdapter adapter;
    WalletTransactionViewModel viewModel;
    List<WalletTransaction> walletTransactionList = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_wallet_transactions;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new WalletTransactionAdapter();
        dataBinding.setIsDataAvailable(false);

        viewModel = new ViewModelProvider(this).get(WalletTransactionViewModel.class);
        viewModel.getTransactions().observe(this, new Observer<List<WalletTransaction>>() {
            @Override
            public void onChanged(List<WalletTransaction> walletTransactions) {
                walletTransactionList = walletTransactions;
                if (walletTransactionList.size() > 0) {
                    adapter.setList(walletTransactionList);
                    adapter.notifyDataSetChanged();
                    dataBinding.setIsDataAvailable(true);
                    dataBinding.textTotalIDs.setText("Total IDs: "+walletTransactionList.size());
                }
            }
        });


        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.recyclerView.setAdapter(adapter);

    }
}
