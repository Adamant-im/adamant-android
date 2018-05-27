package com.dremanovich.adamant_android.ui;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends MvpAppCompatActivity {

    public abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }
}
