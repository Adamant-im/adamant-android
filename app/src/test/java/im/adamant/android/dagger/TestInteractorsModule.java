package im.adamant.android.dagger;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.HasNewMessagesInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SaveContactsInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SendFundsInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.TransferDetailsInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestInteractorsModule {
    @Singleton
    @Provides
    public static ChatUpdatePublicKeyInteractor provideCharUpdatePublicKeyInteractor() {
        return mock(ChatUpdatePublicKeyInteractor.class);
    }

    @Singleton
    @Provides
    public static WalletInteractor provideWalletInteractor() {
        return mock(WalletInteractor.class);
    }

    @Singleton
    @Provides
    public static AuthorizeInteractor provideAuthorizationInteractor() {
        return mock(AuthorizeInteractor.class);
    }

    @Singleton
    @Provides
    public static AccountInteractor provideAccountInteractor() {
        return mock(AccountInteractor.class);
    }

    @Singleton
    @Provides
    public static SecurityInteractor provideKeypairInteractor() {
        return mock(SecurityInteractor.class);
    }

    @Singleton
    @Provides
    public static SwitchPushNotificationServiceInteractor provideSubscribeToPushInteractor() {
        return mock(SwitchPushNotificationServiceInteractor.class);
    }

    @Singleton
    @Provides
    public static SendFundsInteractor provideSendCurrencyInteractor() {
        return mock(SendFundsInteractor.class);
    }

    @Singleton
    @Provides
    public static ServerNodeInteractor provideServerNodeInteractor() {
        return mock(ServerNodeInteractor.class);
    }

    @Singleton
    @Provides
    public static SaveContactsInteractor provideSaveContactsInteractor() {
        return mock(SaveContactsInteractor.class);
    }

    @Singleton
    @Provides
    public static LogoutInteractor provideLogoutInteractor() {
        return mock(LogoutInteractor.class);
    }

    @Singleton
    @Provides
    public static HasNewMessagesInteractor provideHasNewMessagesInteractor() {
        return mock(HasNewMessagesInteractor.class);
    }

    @Singleton
    @Provides
    public static TransferDetailsInteractor transferDetailsInteractor() {
        return mock(TransferDetailsInteractor.class);
    }
}
