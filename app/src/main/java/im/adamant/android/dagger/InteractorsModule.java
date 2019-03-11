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
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.GetContactsInteractor;
import im.adamant.android.interactors.HasNewMessagesInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.interactors.SaveContactsInteractor;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SendFundsInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.mappers.LocalizedChatMapper;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
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
    public static SaveKeypairInteractor provideKeypairInteractor(
            Settings settings,
            KeyStoreCipher keyStoreCipher,
            AdamantApiWrapper apiWrapper
    ) {
        return new SaveKeypairInteractor(settings, apiWrapper, keyStoreCipher);
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
    public static ServerNodeInteractor provideServerNodeInteractor(Settings settings) {
        return new ServerNodeInteractor(settings);
    }

    @Singleton
    @Provides
    public static RefreshChatsInteractor provideRefreshInteractor(
            AdamantApiWrapper api,
            TransactionToMessageMapper messageMapper,
            TransactionToChatMapper chatMapper,
            LocalizedMessageMapper localizedMessageMapper,
            LocalizedChatMapper localizedChatMapper,
            ChatsStorage chatsStorage
    ) {
        return new RefreshChatsInteractor(
                api,
                chatMapper,
                messageMapper,
                localizedMessageMapper,
                localizedChatMapper,
                chatsStorage
        );
    }

    @Singleton
    @Provides
    public static GetContactsInteractor provideGetContactsInteractor(
            ApiKvsProvider apiKvsProvider,
            ChatsStorage chatsStorage,
            KvsHelper kvsHelper
    ) {
        return new GetContactsInteractor(apiKvsProvider, chatsStorage, kvsHelper);
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
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            RefreshChatsInteractor refreshChatsInteractor
    ) {
        return new LogoutInteractor(chatsStorage, settings, api, switchPushNotificationServiceInteractor, refreshChatsInteractor);
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
            Gson gson
    ) {
        return new SecurityInteractor(gson, settings, api, keyStoreCipher);
    }
}
