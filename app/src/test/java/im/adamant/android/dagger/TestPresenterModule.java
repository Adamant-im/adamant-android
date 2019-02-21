package im.adamant.android.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.ui.presenters.ChatsPresenter;
import im.adamant.android.ui.presenters.CreateChatPresenter;
import im.adamant.android.ui.presenters.LoginPresenter;
import im.adamant.android.ui.presenters.MessagesPresenter;
import im.adamant.android.ui.presenters.NodesListPresenter;
import im.adamant.android.ui.presenters.PushSubscriptionPresenter;
import im.adamant.android.ui.presenters.RegistrationPresenter;
import im.adamant.android.ui.presenters.SendFundsPresenter;
import im.adamant.android.ui.presenters.SettingsPresenter;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;
import im.adamant.android.ui.presenters.WalletPresenter;
import io.reactivex.disposables.CompositeDisposable;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestPresenterModule {
    @Singleton
    @Provides
    public static MessagesPresenter provideMessagesPresenter( ){
        return mock(MessagesPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.MESSAGES_SCREEN)
    public static CompositeDisposable provideMessagesComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static NodesListPresenter provideNodesListPresenter() {
        return mock(NodesListPresenter.class);
    }

    @Singleton
    @Provides
    @Named(Screens.NODES_LIST_SCREEN)
    public static CompositeDisposable provideNodesListComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static PushSubscriptionPresenter providePushSubscriptionPresenter(
    ){
        return mock(PushSubscriptionPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.PUSH_SUBSCRIPTION_SCREEN)
    public static CompositeDisposable providePushSubscriptionComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static RegistrationPresenter provideRegistrationPresenter(
    ) {
        return mock(RegistrationPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.REGISTRATION_SCREEN)
    public static CompositeDisposable provideRegistrationComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static ShowQrCodePresenter provideShowQrCodePresenter(
    ) {
        return mock(ShowQrCodePresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.SHOW_QRCODE_SCREEN)
    public static CompositeDisposable provideShowQrCodeComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    @Named(value = Screens.LOGIN_SCREEN)
    public static CompositeDisposable provideLoginComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static LoginPresenter provideLoginPresenter(
    ){
        return mock(LoginPresenter.class);
    }

    @Singleton
    @Provides
    public static ChatsPresenter provideChatsPresenter(
    ){
        return mock(ChatsPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.CHATS_SCREEN)
    public static CompositeDisposable provideChatComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static CreateChatPresenter provideCreateChatPresenter(
    ){
        return mock(CreateChatPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public static CompositeDisposable provideCreateChatComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static SendFundsPresenter provideSendCurrencyPresenter(
    ){
        return mock(SendFundsPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.SEND_CURRENCY_TRANSFER_SCREEN)
    public static CompositeDisposable provideSendCurrencyTransferComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
    ) {
        return mock(SettingsPresenter.class);
    }

    @Singleton
    @Provides
    @Named(Screens.SETTINGS_SCREEN)
    public static CompositeDisposable provideSettingsComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static WalletPresenter provideWalletPresenter(){
        return mock(WalletPresenter.class);
    }

    @Singleton
    @Provides
    @Named(value = Screens.WALLET_SCREEN)
    public static CompositeDisposable provideWalletComposite() {
        return new CompositeDisposable();
    }
}
