package im.adamant.android.dagger;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Constants;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.GetContactsInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SendFundsInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.markdown.AdamantAddressExtractor;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.presenters.ChatsPresenter;
import im.adamant.android.ui.presenters.CreateChatPresenter;
import im.adamant.android.ui.presenters.LoginPresenter;
import im.adamant.android.ui.presenters.MainPresenter;
import im.adamant.android.ui.presenters.MessagesPresenter;
import im.adamant.android.ui.presenters.NodesListPresenter;
import im.adamant.android.ui.presenters.PushSubscriptionPresenter;
import im.adamant.android.ui.presenters.RegistrationPresenter;
import im.adamant.android.ui.presenters.SendFundsPresenter;
import im.adamant.android.ui.presenters.SettingsPresenter;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;
import im.adamant.android.ui.presenters.WalletPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public abstract class PresentersModule {
    @Singleton
    @Provides
    public static MessagesPresenter provideMessagesPresenter(
            Router router,
            RefreshChatsInteractor refreshChatsInteractor,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteraactor,
            MessageFactoryProvider messageFactoryProvider,
            AdamantApiWrapper api,
            ChatsStorage chatsStorage,
            @Named(Screens.MESSAGES_SCREEN) CompositeDisposable subscriptions
    ){
        return new MessagesPresenter(
                router,
                refreshChatsInteractor,
                chatUpdatePublicKeyInteraactor,
                messageFactoryProvider,
                chatsStorage,
                api,
                subscriptions
        );
    }

    @Singleton
    @Provides
    @Named(value = Screens.MESSAGES_SCREEN)
    public static CompositeDisposable provideMessagesComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static NodesListPresenter provideNodesListPresenter(
            ServerNodeInteractor serverNodeInteractor,
            @Named(Screens.NODES_LIST_SCREEN) CompositeDisposable subscriptions
    ) {
        return new NodesListPresenter(
                serverNodeInteractor,
                subscriptions
        );
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
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            @Named(Screens.PUSH_SUBSCRIPTION_SCREEN) CompositeDisposable subscriptions,
            @Named(Constants.UI_SCHEDULER) Scheduler observableScheduler
    ){
        return new PushSubscriptionPresenter(
                switchPushNotificationServiceInteractor,
                subscriptions,
                observableScheduler
        );
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
            Router router,
            AuthorizeInteractor authorizeInteractor,
            @Named(Screens.REGISTRATION_SCREEN) CompositeDisposable subscriptions
    ) {
        return new RegistrationPresenter(router, authorizeInteractor, subscriptions);
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
            QrCodeHelper qrCodeHelper,
            @Named(Screens.SHOW_QRCODE_SCREEN) CompositeDisposable subscriptions
    ) {
        return new ShowQrCodePresenter(qrCodeHelper, subscriptions);
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
            Router router,
            AuthorizeInteractor interactor,
            @Named(Screens.LOGIN_SCREEN) CompositeDisposable subscriptions
    ){
        return new LoginPresenter(router,interactor,subscriptions);
    }

    @Singleton
    @Provides
    public static ChatsPresenter provideChatsPresenter(
            Router router,
            GetContactsInteractor getContactsInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            ChatsStorage chatsStorage,
            @Named(Screens.CHATS_SCREEN) CompositeDisposable subscriptions
    ){
        return new ChatsPresenter(router,getContactsInteractor, refreshChatsInteractor, chatsStorage, subscriptions);
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
            Router router,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            AdamantAddressExtractor adamantAddressExtractor,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteraactor,
            ChatsStorage chatsStorage,
            @Named(Screens.CREATE_CHAT_SCREEN) CompositeDisposable subscriptions
    ){
        return new CreateChatPresenter(
                router,
                wallets,
                chatUpdatePublicKeyInteraactor,
                adamantAddressExtractor,
                chatsStorage,
                subscriptions
        );
    }

    @Singleton
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public static CompositeDisposable provideCreateChatComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            Router router,
            AdamantApiWrapper api,
            SaveKeypairInteractor saveKeypairInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            @Named(Screens.SETTINGS_SCREEN) CompositeDisposable subscriptions
    ) {
        return new SettingsPresenter(
                router,
                api,
                saveKeypairInteractor,
                switchPushNotificationServiceInteractor,
                subscriptions
        );
    }

    @Singleton
    @Provides
    @Named(Screens.SETTINGS_SCREEN)
    public static CompositeDisposable provideSettingsComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static WalletPresenter provideWalletPresenter(
            Router router,
            WalletInteractor walletInteractor,
            @Named(value = Screens.WALLET_SCREEN) CompositeDisposable subscriptions
    ){
        return new WalletPresenter(router, walletInteractor, subscriptions);
    }

    @Singleton
    @Provides
    @Named(value = Screens.WALLET_SCREEN)
    public static CompositeDisposable provideWalletComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    @Named("main")
    public static CompositeDisposable provideMainComposite() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public static MainPresenter provideMainPresenter(
            Router router,
            LogoutInteractor logoutInteractor,
            @Named("main") CompositeDisposable compositeDisposable
    ){
        return new MainPresenter(router, logoutInteractor, compositeDisposable);
    }
}
