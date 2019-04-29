package im.adamant.android.dagger.fragments;

import im.adamant.android.avatars.Avatar;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.ui.presenters.ChatsPresenter;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.adapters.ChatsAdapter;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.fragments.ChatsScreen;
import ru.terrakok.cicerone.Router;

@Module
public class ChatsScreenModule {
    @FragmentScope
    @Provides
    public ChatsAdapter provideAdapter(
            ChatsScreen chatsScreen,
            Avatar avatar
    ){
        return new ChatsAdapter(null, chatsScreen, avatar);
    }

    @FragmentScope
    @Provides
    public static ChatsPresenter provideChatsPresenter(
            Router router,
            ChatInteractor chatInteractor,
            AccountInteractor accountInteractor,
            ChatsStorage chatsStorage
    ){
        return new ChatsPresenter(router, accountInteractor, chatInteractor, chatsStorage);
    }
}
