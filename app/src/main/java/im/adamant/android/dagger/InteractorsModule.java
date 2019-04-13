package im.adamant.android.dagger;

import com.google.gson.Gson;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.interactors.HasNewMessagesInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SaveContactsInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SendFundsInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.interactors.chats.ContactsSource;
import im.adamant.android.interactors.chats.HistoryTransactionsSource;
import im.adamant.android.interactors.chats.LastTransactionInChatsSource;
import im.adamant.android.interactors.chats.NewTransactionsSource;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.mappers.ChatTransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

@Module
public abstract class InteractorsModule {
    @Singleton
    @Provides
    public static ChatUpdatePublicKeyInteractor provideCharUpdatePublicKeyInteractor(AdamantApiWrapper api) {
        return new ChatUpdatePublicKeyInteractor(api);
    }

    @Singleton
    @Provides
    public static WalletInteractor provideWalletInteractor(
            Map<SupportedWalletFacadeType, WalletFacade> wallets
    ) {
        return new WalletInteractor(wallets);
    }

    @Singleton
    @Provides
    public static AuthorizeInteractor provideAuthorizationInteractor(
            AdamantApiWrapper api,
            AdamantKeyGenerator keyGenerator,
            KeyStoreCipher keyStoreCipher,
            Settings settings
    ) {
        return new AuthorizeInteractor(api, keyGenerator, keyStoreCipher, settings);
    }

    @Singleton
    @Provides
    public static AccountInteractor provideAccountInteractor(
            AdamantApiWrapper api,
            Settings settings,
            ChatsStorage chatsStorage
    ) {
        return new AccountInteractor(api, settings, chatsStorage);
    }

    @Singleton
    @Provides
    public static SwitchPushNotificationServiceInteractor provideSubscribeToPushInteractor(
            Settings settings,
            Map<SupportedPushNotificationFacadeType, PushNotificationServiceFacade> facades
    ) {
        return new SwitchPushNotificationServiceInteractor(settings, facades);
    }

    @Singleton
    @Provides
    public static SendFundsInteractor provideSendCurrencyInteractor(
            AdamantApiWrapper api, ChatsStorage chatsStorage, MessageFactoryProvider messageFactoryProvider
    ) {
        return new SendFundsInteractor(api, chatsStorage, messageFactoryProvider);
    }

    @Singleton
    @Provides
    public static ServerNodeInteractor provideServerNodeInteractor(AdamantApiWrapper api, Settings settings) {
        return new ServerNodeInteractor(api, settings);
    }

    @Singleton
    @Provides
    public static ChatInteractor provideGetChatListInteractor(
            NewTransactionsSource newTransactionsSource,
            LastTransactionInChatsSource lastTransactionInChatsSource,
            HistoryTransactionsSource historyTransactionsSource,
            ContactsSource contactsSource,
            ChatsStorage chatsStorage,
            ChatTransactionToChatMapper chatMapper,
            TransactionToMessageMapper messageMapper
    ) {
        return new ChatInteractor(
                newTransactionsSource,
                lastTransactionInChatsSource,
                historyTransactionsSource,
                contactsSource,
                chatsStorage,
                chatMapper,
                messageMapper
        );
    }


    @Singleton
    @Provides
    public static SaveContactsInteractor provideSaveContactsInteractor(ApiKvsProvider apiKvsProvider, ChatsStorage chatsStorage, KvsHelper kvsHelper) {
        return new SaveContactsInteractor(apiKvsProvider, chatsStorage, kvsHelper);
    }

    @Singleton
    @Provides
    public static LogoutInteractor provideLogoutInteractor(
            ChatsStorage chatsStorage,
            Settings settings,
            AdamantApiWrapper api,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor
    ) {
        return new LogoutInteractor(chatsStorage, settings, api, switchPushNotificationServiceInteractor);
    }

    @Singleton
    @Provides
    public static HasNewMessagesInteractor provideHasNewMessagesInteractor(
            Settings settings,
            AdamantApiWrapper api
    ) {
        return new HasNewMessagesInteractor(api, settings);
    }

    @Singleton
    @Provides
    public static SecurityInteractor provideSecurityInteractor(
            Settings settings,
            AdamantApiWrapper api,
            KeyStoreCipher keyStoreCipher,
            Gson gson,
            SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor
    ) {
        return new SecurityInteractor(gson, settings, api, keyStoreCipher, pushNotificationServiceInteractor);
    }
}
