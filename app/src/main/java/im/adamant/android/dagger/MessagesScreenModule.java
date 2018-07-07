package im.adamant.android.dagger;

import im.adamant.android.Screens;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.presenters.MessagesPresenter;
import im.adamant.android.ui.adapters.MessagesAdapter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
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
    public MessagesAdapter provideAdapter(MessageFactoryProvider messageFactoryProvider){
        return new MessagesAdapter(messageFactoryProvider,null);
    }
}
