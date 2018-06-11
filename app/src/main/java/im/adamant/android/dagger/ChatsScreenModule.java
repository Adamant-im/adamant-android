package im.adamant.android.dagger;

import im.adamant.android.Screens;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.presenters.ChatsPresenter;
import im.adamant.android.ui.ChatsScreen;
import im.adamant.android.ui.adapters.ChatsAdapter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class ChatsScreenModule {
    @ActivityScope
    @Provides
    public ChatsPresenter provideLoginPresenter(
            Router router,
            ChatsInteractor interactor,
            @Named(Screens.CHATS_SCREEN) CompositeDisposable subscriptions
    ){
        return new ChatsPresenter(router,interactor,subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.CHATS_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    public ChatsAdapter provideAdapter(ChatsScreen chatsScreen){
        return new ChatsAdapter(null, chatsScreen);
    }
}
