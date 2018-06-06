package com.dremanovich.adamant_android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.interactors.AuthorizeInteractor;
import com.dremanovich.adamant_android.ui.mvp_view.LoginView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class LoginPresenter extends BasePresenter<LoginView> {
    private Router router;
    private AuthorizeInteractor authorizeInteractor;

    public LoginPresenter(
            Router router,
            AuthorizeInteractor authorizeInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.authorizeInteractor = authorizeInteractor;
    }

    public void onClickLoginButton(String passPhrase) {
        if (!authorizeInteractor.isValidPassphrase(passPhrase)){
            getViewState().loginError(R.string.wrong_passphrase);
        }

        getViewState().lockAuthorization();

        Disposable subscription = authorizeInteractor.authorize(passPhrase).subscribe(
                (authorize)->{
                    if (authorize.isSuccess()){
                        router.navigateTo(Screens.CHATS_SCREEN);
                    } else {

                        //TODO: Oh my god, somebody fix this, please.
                        //PWA adamantServerApi.js, line: 129
                        if ("Account not found".equalsIgnoreCase(authorize.getError())) {
                            onClickCreateNewAccount(passPhrase);
                        } else {
                            Log.e("ERR", authorize.getError());
                            router.showSystemMessage(authorize.getError());
                        }
                    }
                },
                (error) -> {
                    router.showSystemMessage(error.getMessage());
                },
                () -> getViewState().unLockAuthorization()
        );

       subscriptions.add(subscription);
    }

    public void onClickGeneratePassphrase() {
        getViewState().passPhraseWasGenerated(
                authorizeInteractor.generatePassPhrase()
        );
    }

    public void onClickCreateNewAccount(String passPhrase) {
        if (!authorizeInteractor.isValidPassphrase(passPhrase)){
            getViewState().loginError(R.string.wrong_passphrase);
        }

        getViewState().lockAuthorization();

        Disposable subscription = authorizeInteractor
                .createNewAccount(passPhrase)
                .subscribe(
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
                        },
                        () -> getViewState().unLockAuthorization()
                );

        subscriptions.add(subscription);
    }
}
