package com.dremanovich.adamant_android.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.ui.mvp_view.MessagesView;

import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

public class MessagesPresenter extends MvpPresenter<MessagesView>{
    private Router router;
    private ChatsInteractor interactor;
    private CompositeDisposable subscriptions;

    public MessagesPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        this.router = router;
        this.interactor = interactor;
        this.subscriptions = subscriptions;
    }
}
