package im.adamant.android.dagger.fragments;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.ui.adapters.ChatsAdapter;
import im.adamant.android.ui.fragments.ChatsScreen;
import im.adamant.android.ui.presenters.ChatsPresenter;
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
            AccountInteractor accountInteractor
    ){
        return new ChatsPresenter(router, accountInteractor, chatInteractor);
    }
}
