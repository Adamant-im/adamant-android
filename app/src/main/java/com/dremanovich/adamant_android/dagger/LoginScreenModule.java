package com.dremanovich.adamant_android.dagger;

import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.encryption.KeyGenerator;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.interactors.AuthorizeInteractor;
import com.dremanovich.adamant_android.presenters.LoginPresenter;

import javax.inject.Named;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class LoginScreenModule {

    @ActivityScope
    @Provides
    public LoginPresenter provideLoginPresenter(
            Router router,
            AuthorizeInteractor interactor,
            @Named(Screens.LOGIN_SCREEN) CompositeDisposable subscriptions
    ){
        return new LoginPresenter(router,interactor,subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.LOGIN_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
