package com.dremanovich.adamant_android.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends MvpView> extends MvpPresenter<V> {
    protected final CompositeDisposable subscriptions;

    public BasePresenter(CompositeDisposable subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }
}
