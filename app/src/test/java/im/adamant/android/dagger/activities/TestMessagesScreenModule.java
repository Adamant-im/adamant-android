package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.adapters.MessagesAdapter;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

import static org.mockito.Mockito.mock;

@Module
public class TestMessagesScreenModule {
    @ActivityScope
    @Provides
    public MessagesAdapter provideAdapter() {
        return mock(MessagesAdapter.class);
    }
}
