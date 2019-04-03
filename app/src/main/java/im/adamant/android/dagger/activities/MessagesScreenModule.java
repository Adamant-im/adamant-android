package im.adamant.android.dagger.activities;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.ui.presenters.MessagesPresenter;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.adapters.MessagesAdapter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import ru.terrakok.cicerone.Router;

@Module
public class MessagesScreenModule {
    @ActivityScope
    @Provides
    public MessagesAdapter provideAdapter(MessageFactoryProvider messageFactoryProvider){
        return new MessagesAdapter(messageFactoryProvider,null);
    }

    @ActivityScope
    @Provides
    public static MessagesPresenter provideMessagesPresenter(
            Router router,
            RefreshChatsInteractor refreshChatsInteractor,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteraactor,
            MessageFactoryProvider messageFactoryProvider,
            AdamantApiWrapper api,
            ChatsStorage chatsStorage
    ) {
        return new MessagesPresenter(
                router,
                refreshChatsInteractor,
                chatUpdatePublicKeyInteraactor,
                messageFactoryProvider,
                chatsStorage,
                api
        );
    }
}
