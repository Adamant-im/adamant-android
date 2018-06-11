package im.adamant.android.dagger;

import im.adamant.android.Screens;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.presenters.CreateChatPresenter;

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
