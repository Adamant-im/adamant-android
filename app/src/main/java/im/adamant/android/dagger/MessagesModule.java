package im.adamant.android.dagger;

import android.content.Context;

import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.ui.mappers.ChatTransactionToChatMapper;
import im.adamant.android.ui.mappers.LocalizedChatMapper;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.factories.AdamantBasicMessageFactory;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.AdamantTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.BinanceTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.EthereumTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.FallbackMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

@Module
public abstract class MessagesModule {
    @Singleton
    @Provides
    public static ChatsStorage provideChatsStorage() {
        return new ChatsStorage();
    }


    @Singleton
    @Provides
    public static MessageFactoryProvider provideMessageFactoryProvider(
            GsonBuilder gsonBuilder,
            AdamantMarkdownProcessor adamantAddressProcessor,
            Encryptor encryptor,
            AdamantApiWrapper api,
            PublicKeyStorage publicKeyStorage,
            Avatar avatar
    ) {
        MessageFactoryProvider provider = new MessageFactoryProvider();

        provider.registerFactory(
                SupportedMessageListContentType.ADAMANT_BASIC,
                new AdamantBasicMessageFactory(adamantAddressProcessor, encryptor, api, publicKeyStorage, avatar)
        );

        provider.registerFactory(
                SupportedMessageListContentType.FALLBACK,
                new FallbackMessageFactory(adamantAddressProcessor, avatar)
        );

        provider.registerFactory(
                SupportedMessageListContentType.ETHEREUM_TRANSFER,
                new EthereumTransferMessageFactory(adamantAddressProcessor, avatar)
        );

        provider.registerFactory(
                SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION,
                new AdamantPushSubscriptionMessageFactory(gsonBuilder, encryptor, api, publicKeyStorage)
        );

        provider.registerFactory(
                SupportedMessageListContentType.BINANCE_TRANSFER,
                new BinanceTransferMessageFactory(adamantAddressProcessor, avatar)
        );

        provider.registerFactory(
                SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE,
                new AdamantTransferMessageFactory(adamantAddressProcessor, encryptor, api, publicKeyStorage, avatar)
        );

        return provider;
    }

    @Singleton
    @Provides
    public static TransactionToMessageMapper providesTransactionsToMessageMapper(
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage,
            AdamantApiWrapper api,
            MessageFactoryProvider factoryProvider
    ) {
        return new TransactionToMessageMapper(encryptor, publicKeyStorage, api, factoryProvider);
    }

    @Singleton
    @Provides
    public static TransactionToChatMapper providesTransactionsToChatMapper(AdamantApiWrapper api, PublicKeyStorage publicKeyStorage) {
        return new TransactionToChatMapper(api, publicKeyStorage);
    }

    @Singleton
    @Provides
    public static ChatTransactionToChatMapper providesChatTransactionToChatMapper(AdamantApiWrapper api) {
        return new ChatTransactionToChatMapper(api);
    }

    @Singleton
    @Provides
    public static LocalizedMessageMapper providesLocalizedMessageMapper(Context ctx) {
        return new LocalizedMessageMapper(ctx);
    }

    @Singleton
    @Provides
    public static LocalizedChatMapper providesLocalizedChatMapper(Context ctx) {
        return new LocalizedChatMapper(ctx);
    }

}
