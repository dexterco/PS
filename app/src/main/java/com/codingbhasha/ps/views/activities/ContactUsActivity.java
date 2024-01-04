package com.codingbhasha.ps.views.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityContactUsBinding;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class ContactUsActivity extends BaseActivity<ActivityContactUsBinding> {
    @Override
    public int getLayoutResId() {
        return R.layout.activity_contact_us;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
