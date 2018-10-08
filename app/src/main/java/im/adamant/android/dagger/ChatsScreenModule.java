package im.adamant.android.dagger;

import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.interactors.GetContactsInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.presenters.ChatsPresenter;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.adapters.ChatsAdapter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.fragments.ChatsScreen;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class ChatsScreenModule {
    @FragmentScope
    @Provides
    public ChatsPresenter provideChatsPresenter(
            Router router,
            GetContactsInteractor getContactsInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            ChatsStorage chatsStorage,
            @Named(Screens.CHATS_SCREEN) CompositeDisposable subscriptions
    ){
        return new ChatsPresenter(router,getContactsInteractor, refreshChatsInteractor, chatsStorage, subscriptions);
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.CHATS_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @FragmentScope
    @Provides
    public ChatsAdapter provideAdapter(
            ChatsScreen chatsScreen,
            @Named(Screens.CHATS_SCREEN) CompositeDisposable compositeDisposable,
            Avatar avatar
    ){
        return new ChatsAdapter(null, chatsScreen, compositeDisposable, avatar);
    }
}
