package im.adamant.android.dagger;

import im.adamant.android.dagger.activities.ActivityScope;
import im.adamant.android.dagger.activities.LoginScreenModule;
import im.adamant.android.dagger.activities.MainScreenModule;
import im.adamant.android.dagger.activities.MessagesScreenModule;
import im.adamant.android.dagger.activities.NodesListScreenModule;
import im.adamant.android.dagger.activities.PushSubscriptionScreenModule;
import im.adamant.android.dagger.activities.RegistrationScreenModule;
import im.adamant.android.dagger.activities.ScanQrCodeScreenModule;
import im.adamant.android.dagger.activities.SendCurrencyTransferScreenModule;
import im.adamant.android.dagger.activities.ShowQrCodeScreenModule;
import im.adamant.android.dagger.activities.SplashScreenModule;
import im.adamant.android.dagger.receivers.BootCompletedBroadcastReceiverModule;
import im.adamant.android.dagger.receivers.ReceiverScope;
import im.adamant.android.dagger.services.AdamantBalanceUpdateServiceModule;
import im.adamant.android.dagger.services.AdamantFirebaseMessagingServiceModule;
import im.adamant.android.dagger.services.AdamantLocalMessagingServiceModule;
import im.adamant.android.dagger.services.SaveContactsServiceModule;
import im.adamant.android.dagger.services.ServerNodePingServiceModule;
import im.adamant.android.dagger.services.ServiceScope;
import im.adamant.android.interactors.LogoutInteractor;

import im.adamant.android.receivers.BootCompletedBroadcast;
import im.adamant.android.services.AdamantBalanceUpdateService;
import im.adamant.android.services.AdamantFirebaseMessagingService;
import im.adamant.android.services.AdamantLocalMessagingService;
import im.adamant.android.services.SaveContactsService;
import im.adamant.android.services.ServerNodesPingService;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.MessagesScreen;
import im.adamant.android.ui.NodesListScreen;
import im.adamant.android.ui.PushSubscriptionScreen;
import im.adamant.android.ui.RegistrationScreen;
import im.adamant.android.ui.ScanQrCodeScreen;
import im.adamant.android.ui.SendFundsScreen;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.ui.SplashScreen;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import im.adamant.android.ui.presenters.MainPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module(includes = {AndroidSupportInjectionModule.class})
public abstract class AppModule {

    //--Activities

    @ActivityScope
    @ContributesAndroidInjector(modules = {LoginScreenModule.class})
    public abstract LoginScreen loginScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MessagesScreenModule.class})
    public abstract MessagesScreen messagesScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MainScreenModule.class})
    public abstract MainScreen createMainScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {ScanQrCodeScreenModule.class})
    public abstract ScanQrCodeScreen createScanQrCodeScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {SplashScreenModule.class})
    public abstract SplashScreen createSplashScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {RegistrationScreenModule.class})
    public abstract RegistrationScreen createRegistrationScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {ShowQrCodeScreenModule.class})
    public abstract ShowQrCodeScreen createShowQrCodeScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {SendCurrencyTransferScreenModule.class})
    public abstract SendFundsScreen createSendCurrencyTransferScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {NodesListScreenModule.class})
    public abstract NodesListScreen createNodesListScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {PushSubscriptionScreenModule.class})
    public abstract PushSubscriptionScreen createPushSubscriptionScreenInjector();


    //--Services

    @ServiceScope
    @ContributesAndroidInjector(modules = {ServerNodePingServiceModule.class})
    public abstract ServerNodesPingService createServerNodePingService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {AdamantBalanceUpdateServiceModule.class})
    public abstract AdamantBalanceUpdateService createBalanceUpdateService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {SaveContactsServiceModule.class})
    public abstract SaveContactsService createSaveContatactsService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {AdamantFirebaseMessagingServiceModule.class})
    public abstract AdamantFirebaseMessagingService createAdamantFirebaseMessagingService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {AdamantLocalMessagingServiceModule.class})
    public abstract AdamantLocalMessagingService createAdamantLocalMessagingService();


    //--Receivers

    @ReceiverScope
    @ContributesAndroidInjector(modules = {BootCompletedBroadcastReceiverModule.class})
    public abstract BootCompletedBroadcast createBootCompletedBroadcastReceiver();


    //--presenters

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
