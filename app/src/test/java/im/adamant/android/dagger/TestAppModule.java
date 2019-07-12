package im.adamant.android.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import im.adamant.android.dagger.activities.ActivityScope;
import im.adamant.android.dagger.activities.AllTransactionsScreenModule;
import im.adamant.android.dagger.activities.TestAllTransactionsScreenModule;
import im.adamant.android.dagger.activities.TestLoginScreenModule;
import im.adamant.android.dagger.activities.TestMainScreenModule;
import im.adamant.android.dagger.activities.TestMessagesScreenModule;
import im.adamant.android.dagger.activities.TestNodesListScreenModule;
import im.adamant.android.dagger.activities.TestPushSubscriptionScreenModule;
import im.adamant.android.dagger.activities.TestRegistrationScreenModule;
import im.adamant.android.dagger.activities.TestScanQrCodeScreenModule;
import im.adamant.android.dagger.activities.TestSendCurrencyTransferScreenModule;
import im.adamant.android.dagger.activities.TestShowQrCodeScreenModule;
import im.adamant.android.dagger.activities.TestSplashScreenModule;
import im.adamant.android.dagger.activities.TestTransferDetailsScreenModule;
import im.adamant.android.dagger.activities.TransferDetailsScreenModule;
import im.adamant.android.dagger.receivers.ReceiverScope;
import im.adamant.android.dagger.receivers.TestBootCompletedBroadcastReceiverModule;
import im.adamant.android.dagger.services.ServiceScope;
import im.adamant.android.dagger.services.TestAdamantBalanceUpdateServiceModule;
import im.adamant.android.dagger.services.TestAdamantFirebaseMessagingServiceModule;
import im.adamant.android.dagger.services.TestAdamantLocalMessagingServiceModule;
import im.adamant.android.dagger.services.TestSaveContactsServiceModule;
import im.adamant.android.dagger.services.TestServerNodePingServiceModule;
import im.adamant.android.receivers.BootCompletedBroadcast;
import im.adamant.android.services.AdamantBalanceUpdateService;
import im.adamant.android.services.AdamantFirebaseMessagingService;
import im.adamant.android.services.AdamantLocalMessagingService;
import im.adamant.android.services.SaveContactsService;
import im.adamant.android.services.ServerNodesPingService;
import im.adamant.android.ui.AllTransactionsScreen;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.MessagesScreen;
import im.adamant.android.ui.NodesListScreen;
import im.adamant.android.ui.RegistrationScreen;
import im.adamant.android.ui.ScanQrCodeScreen;
import im.adamant.android.ui.SendFundsScreen;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.ui.SplashScreen;
import im.adamant.android.ui.TransferDetailsScreen;

@Module(includes = {AndroidSupportInjectionModule.class})
public abstract class TestAppModule {

    //--Activities

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestLoginScreenModule.class})
    public abstract LoginScreen loginScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestMessagesScreenModule.class})
    public abstract MessagesScreen messagesScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestMainScreenModule.class})
    public abstract MainScreen createMainScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestScanQrCodeScreenModule.class})
    public abstract ScanQrCodeScreen createScanQrCodeScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestSplashScreenModule.class})
    public abstract SplashScreen createSplashScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestRegistrationScreenModule.class})
    public abstract RegistrationScreen createRegistrationScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestShowQrCodeScreenModule.class})
    public abstract ShowQrCodeScreen createShowQrCodeScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestSendCurrencyTransferScreenModule.class})
    public abstract SendFundsScreen createSendCurrencyTransferScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestNodesListScreenModule.class})
    public abstract NodesListScreen createNodesListScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestAllTransactionsScreenModule.class})
    public abstract AllTransactionsScreen createAllTransactionsScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TestTransferDetailsScreenModule.class})
    public abstract TransferDetailsScreen createTransferDetailsScreenInjector();


    //--Services

    @ServiceScope
    @ContributesAndroidInjector(modules = {TestServerNodePingServiceModule.class})
    public abstract ServerNodesPingService createServerNodePingService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {TestAdamantBalanceUpdateServiceModule.class})
    public abstract AdamantBalanceUpdateService createBalanceUpdateService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {TestSaveContactsServiceModule.class})
    public abstract SaveContactsService createSaveContatactsService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {TestAdamantFirebaseMessagingServiceModule.class})
    public abstract AdamantFirebaseMessagingService createAdamantFirebaseMessagingService();

    @ServiceScope
    @ContributesAndroidInjector(modules = {TestAdamantLocalMessagingServiceModule.class})
    public abstract AdamantLocalMessagingService createAdamantLocalMessagingService();


    //--Receivers

    @ReceiverScope
    @ContributesAndroidInjector(modules = {TestBootCompletedBroadcastReceiverModule.class})
    public abstract BootCompletedBroadcast createBootCompletedBroadcastReceiver();
}
