package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.ui.adapters.ChatsAdapter;
import im.adamant.android.ui.fragments.ChatsScreen;
import io.reactivex.disposables.CompositeDisposable;

import static org.mockito.Mockito.mock;

@Module
public class TestChatsScreenModule {
    @FragmentScope
    @Provides
    public ChatsAdapter provideAdapter() {
        return mock(ChatsAdapter.class);
    }
}
