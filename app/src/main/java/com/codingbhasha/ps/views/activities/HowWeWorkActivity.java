package com.codingbhasha.ps.views.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityContactUsBinding;
import com.codingbhasha.ps.databinding.ActivityHowWeWorkBinding;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class HowWeWorkActivity extends BaseActivity<ActivityHowWeWorkBinding> {
    @Override
    public int getLayoutResId() {
        return R.layout.activity_how_we_work;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
