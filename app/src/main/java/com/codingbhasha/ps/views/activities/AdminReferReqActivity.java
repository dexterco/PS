package com.codingbhasha.ps.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.ReferReqAdapter;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityAdminReferReqBinding;
import com.codingbhasha.ps.model.ReferReq;
import com.codingbhasha.ps.viewmodel.ReferReqViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AdminReferReqActivity extends BaseActivity<ActivityAdminReferReqBinding> {
    List<ReferReq> referReqList = new ArrayList<>();
    ReferReqAdapter adapter;
    ReferReqViewModel viewModel;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_admin_refer_req;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("referPref", Context.MODE_PRIVATE);
        editor = preferences.edit();

        dataBinding.toolbar.setTitle("Refer Requests");
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://console.firebase.google.com/u/2/project/psapp-699b8/firestore/data~2F")
                .setApiKey("AIzaSyADBFSbh2EH-IwfFzbu6UQFyXiRetZYvFI")
                .setApplicationId("psapp-699b8")
                .build();
        FirebaseAuth auth1 = FirebaseAuth.getInstance();
        FirebaseAuth auth2;

        try {
            FirebaseApp app = FirebaseApp.initializeApp(getApplicationContext(), options, "PS");
            auth2 = FirebaseAuth.getInstance(app);
        } catch (IllegalStateException e) {
            auth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("PS"));
        }


        adapter = new ReferReqAdapter(this, preferences.getInt("memberId", 100000), auth1, auth2);

        viewModel = new ViewModelProvider(this).get(ReferReqViewModel.class);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        viewModel.getReferReqs().observe(this, new Observer<List<ReferReq>>() {
            @Override
            public void onChanged(List<ReferReq> referReqs) {
                referReqList = referReqs;
                if (referReqList.size() > 0) {
                    adapter.setList(referReqList);
                    adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }
        });

        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.recyclerView.setAdapter(adapter);
    }
}
