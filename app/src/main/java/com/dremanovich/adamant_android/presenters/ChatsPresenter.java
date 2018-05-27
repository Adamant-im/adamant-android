package com.dremanovich.adamant_android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.mvp_view.ChatsView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends MvpPresenter<ChatsView> {
    private Router router;
    private ChatsInteractor interactor;
    private CompositeDisposable subscriptions;

    public ChatsPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        this.router = router;
        this.interactor = interactor;
        this.subscriptions = subscriptions;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        Disposable subscription = interactor
                .loadChats()
                .subscribe(
                    (list -> {
                        getViewState().showChats(list);
                    }),
                    ((error) -> router.showSystemMessage(error.getMessage()))
                );

        subscriptions.add(subscription);
    }

    public void onChatWasSelected(Chat chat){
        router.navigateTo(Screens.MESSAGES_SCREEN, chat);
    }
}
