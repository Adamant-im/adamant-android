package com.dremanovich.adamant_android.dagger;

import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.presenters.MessagesPresenter;
import com.dremanovich.adamant_android.ui.ChatsScreen;
import com.dremanovich.adamant_android.ui.adapters.ChatsAdapter;
import com.dremanovich.adamant_android.ui.adapters.MessagesAdapter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class MessagesScreenModule {
    @ActivityScope
    @Provides
    public MessagesPresenter provideMessagesPresenter(
            Router router,
            ChatsInteractor interactor,
            @Named(Screens.MESSAGES_SCREEN) CompositeDisposable subscriptions
    ){
        return new MessagesPresenter(router,interactor,subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.MESSAGES_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    public MessagesAdapter provideAdapter(){
        return new MessagesAdapter(null);
    }
}
