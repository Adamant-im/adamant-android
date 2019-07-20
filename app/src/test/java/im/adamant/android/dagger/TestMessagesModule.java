package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.mappers.LocalizedChatMapper;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestMessagesModule {
    @Singleton
    @Provides
    public static ChatsStorage provideChatsStorage() {
        return mock(ChatsStorage.class);
    }

    @Singleton
    @Provides
    public static MessageFactoryProvider provideMessageFactoryProvider() {
        return mock(MessageFactoryProvider.class);
    }

    @Singleton
    @Provides
    public static TransactionToMessageMapper providesTransactionsToMessageMapper() {
        return mock(TransactionToMessageMapper.class);
    }

    @Singleton
    @Provides
    public static TransactionToChatMapper providesTransactionsToChatMapper() {
        return mock(TransactionToChatMapper.class);
    }

    @Singleton
    @Provides
    public static LocalizedMessageMapper providesLocalizedMessageMapper() {
        return  mock(LocalizedMessageMapper.class);
    }

    @Singleton
    @Provides
    public static LocalizedChatMapper providesLocalizedChatMapper() {
        return mock(LocalizedChatMapper.class);
    }
}
