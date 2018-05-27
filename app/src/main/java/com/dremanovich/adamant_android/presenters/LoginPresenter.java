package com.dremanovich.adamant_android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.interactors.AuthorizeInteractor;
import com.dremanovich.adamant_android.ui.mvp_view.LoginView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {
    private Router router;
    private AuthorizeInteractor authorizeInteractor;
    private CompositeDisposable subscriptions;

    public LoginPresenter(
            Router router,
            AuthorizeInteractor authorizeInteractor,
            CompositeDisposable subscriptions
    ) {
        this.router = router;
        this.authorizeInteractor = authorizeInteractor;
        this.subscriptions = subscriptions;
    }

    public void onClickLoginButton(String passPhrase) {
        //TODO: Need lock button
       Disposable subscription = authorizeInteractor.authorize(passPhrase).subscribe(
                (authorize)->{
                    if (authorize.isSuccess()){
                        router.navigateTo(Screens.CHATS_SCREEN);
                    } else {
                        Log.e("ERR", authorize.getError());
                        router.showSystemMessage(authorize.getError());
                    }
                },
                (error) -> {
                    router.showSystemMessage(error.getMessage());
                }
        );

       subscriptions.add(subscription);
    }
}
