package com.dremanovich.adamant_android.dagger;

import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.presenters.ChatsPresenter;
import com.dremanovich.adamant_android.presenters.CreateChatPresenter;
import com.dremanovich.adamant_android.ui.mvp_view.CreateChatView;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class CreateChatScreenModule {
    @ActivityScope
    @Provides
    public CreateChatPresenter provideLoginPresenter(
            Router router,
            ChatsInteractor interactor,
            @Named(Screens.CREATE_CHAT_SCREEN) CompositeDisposable subscriptions
    ){
        return new CreateChatPresenter(router,interactor,subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
