package com.codingbhasha.ps.views.activities;

import androidx.annotation.Nullable;

import android.os.Bundle;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityPamphletBinding;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class pamphlet extends BaseActivity<ActivityPamphletBinding> {

    @Override
    public int getLayoutResId() {
        return R.layout.activity_pamphlet;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataBinding.toolbar.setTitle("Pamphlet");
    }
}