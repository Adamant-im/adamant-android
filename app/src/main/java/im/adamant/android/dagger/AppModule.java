package im.adamant.android.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import dagger.multibindings.IntoMap;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.avatars.AvatarGraphics;
import im.adamant.android.avatars.AvatarThemesProvider;
import im.adamant.android.avatars.CachedAvatar;
import im.adamant.android.avatars.RoundWithBorderAvatar;
import im.adamant.android.avatars.SquareAvatar;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.interactors.wallets.AdamantWalletFacade;
import im.adamant.android.interactors.wallets.BinanceWalletFacade;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.interactors.wallets.EthereumWalletFacade;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeTypeKey;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.helpers.NaivePublicKeyStorageImpl;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.GetContactsInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.interactors.SaveContactsInteractor;
import im.adamant.android.interactors.SendMessageInteractor;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.services.AdamantBalanceUpdateService;
import im.adamant.android.services.AdamantFirebaseMessagingService;
import im.adamant.android.services.SaveContactsService;
import im.adamant.android.services.SaveSettingsService;
import im.adamant.android.services.ServerNodesPingService;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.MessagesScreen;
import im.adamant.android.ui.RegistrationScreen;
import im.adamant.android.ui.ScanQrCodeScreen;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.ui.SplashScreen;
import im.adamant.android.ui.mappers.LocalizedChatMapper;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;

import com.google.gson.Gson;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.factories.AdamantBasicMessageFactory;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.AdamantTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.BinanceTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.EthereumTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.FallbackMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.presenters.MainPresenter;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

@Module(includes = {AndroidSupportInjectionModule.class})
public abstract class AppModule {

    @Singleton
    @Provides
    public static Gson provideGson() {
        return new Gson();
    }

    @Singleton
    @Provides
    public static AvatarThemesProvider provideAvatarThemes() {
        return new AvatarThemesProvider();
    }

    @Singleton
    @Provides
    public static AvatarGraphics provideAvatarGraphics(AvatarThemesProvider avatarThemesProvider) {
        return new AvatarGraphics(avatarThemesProvider);
    }

    @Singleton
    @Provides
    public static Avatar provideAvatar(Context context, AvatarGraphics graphics){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int borderSizePx = (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1.0f,
                displayMetrics
        );

        int paddingSizePx = (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8.0f,
                displayMetrics
        );

        return new CachedAvatar(
                new RoundWithBorderAvatar(
                        new SquareAvatar(graphics),
                        paddingSizePx,
                        borderSizePx
                ),
                1024 * 1024 * 10 // 10Mb
        );
    }

    @Singleton
    @Provides
    public static KeyStoreCipher provideKeyStoreCipher(Gson gson, Context context) {
        return new KeyStoreCipher(gson, context);
    }

    @Singleton
    @Provides
    public static List<Locale> provideSupportedLocale() {
        Locale ru = new Locale("ru");
        Locale en = new Locale("en");

        return Arrays.asList(en, ru);
    }

    @Singleton
    @Provides
    public static Settings provideSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return new Settings(preferences);
    }

    @Singleton
    @Provides
    public static SeedCalculator provideSeedCalculator() {
        return new SeedCalculator();
    }

    @Singleton
    @Provides
    public static MnemonicGenerator provideMnemonic(){
        return new MnemonicGenerator(English.INSTANCE);
    }

    @Singleton
    @Provides
    public static LazySodium provideLazySodium() {
        SodiumAndroid sodium = new SodiumAndroid();
        return new LazySodiumAndroid(sodium);
    }

    @Singleton
    @Provides
    public static AdamantKeyGenerator providesKeyGenerator(SeedCalculator seedCalculator, MnemonicGenerator mnemonicGenerator, LazySodium sodium) {
        return new AdamantKeyGenerator(seedCalculator, mnemonicGenerator, sodium);
    }

    @Singleton
    @Provides
    public static Encryptor providesMessageEncryptor(LazySodium sodium) {
        return new Encryptor(sodium);
    }

    @Singleton
    @Provides
    public static ChatsStorage provideChatsStorage() {
        return new ChatsStorage();
    }

    @Singleton
    @Provides
    public static KvsHelper provideKvsHelper(Encryptor encryptor, AdamantApiWrapper api, Gson gson) {
        return new KvsHelper(api, encryptor, gson);
    }

    @Singleton
    @Provides
    public static ApiKvsProvider provideApiKvsProvider(AdamantApiWrapper adamantApiWrapper) {
        return new ApiKvsProvider(adamantApiWrapper);
    }

    @Singleton
    @Provides
    public static MessageFactoryProvider provideMessageFactoryProvider(
            AdamantAddressProcessor adamantAddressProcessor,
            Encryptor encryptor,
            AdamantApiWrapper api,
            Avatar avatar
    ) {
        MessageFactoryProvider provider = new MessageFactoryProvider();

        provider.registerFactory(
                SupportedMessageListContentType.ADAMANT_BASIC,
                new AdamantBasicMessageFactory(adamantAddressProcessor, encryptor, api, avatar)
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
                new AdamantPushSubscriptionMessageFactory(encryptor, api)
        );

        provider.registerFactory(
                SupportedMessageListContentType.BINANCE_TRANSFER,
                new BinanceTransferMessageFactory(adamantAddressProcessor, avatar)
        );

        provider.registerFactory(
                SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE,
                new AdamantTransferMessageFactory(adamantAddressProcessor, avatar)
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
    public static LocalizedMessageMapper providesLocalizedMessageMapper(Context ctx) {
        return new LocalizedMessageMapper(ctx);
    }

    @Singleton
    @Provides
    public static LocalizedChatMapper providesLocalizedChatMapper(Context ctx) {
        return new LocalizedChatMapper(ctx);
    }

    @Singleton
    @Provides
    public static AdamantApiWrapper provideAdamantApiWrapper(Settings settings, AdamantKeyGenerator keyGenerator) {
        return new AdamantApiWrapper(settings.getNodes(), keyGenerator);
    }

    @Singleton
    @Provides
    public static PublicKeyStorage providePublicKeyStorage(AdamantApiWrapper api) {
        return new NaivePublicKeyStorageImpl(api);
    }

    @Singleton
    @Provides
    public static Cicerone<Router> provideCicerone() {
        return Cicerone.create();
    }

    @Singleton
    @Provides
    public static Router provideRouter(Cicerone<Router> cicerone) {
        return cicerone.getRouter();
    }

    @Singleton
    @Provides
    public static NavigatorHolder provideNavigatorHolder(Cicerone<Router> cicerone) {
        return cicerone.getNavigatorHolder();
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
    public static SubscribeToPushInteractor provideSubscribeToPushInteractor(
            Settings settings,
            AdamantApiWrapper api,
            MessageFactoryProvider messageFactoryProvider,
            SendMessageInteractor sendMessageInteractor
    ) {
        return new SubscribeToPushInteractor(settings, api, messageFactoryProvider, sendMessageInteractor);
    }

    @Singleton
    @Provides
    public static ServerNodeInteractor provideServerNodeInteractor(Settings settings) {
        return new ServerNodeInteractor(settings);
    }

    @Singleton
    @Provides
    public static AdamantAddressProcessor provideAdamantAddressProcessor() {
        return new AdamantAddressProcessor();
    }

    @Singleton
    @Provides
    public static SendMessageInteractor provideSendMessageInteractor(
            AdamantApiWrapper api,
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage
    ){
        return new SendMessageInteractor(
                api, encryptor, publicKeyStorage
        );
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
    ){
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

    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.ADM)
    @Singleton
    @Provides
    public static WalletFacade provideAdamantInfoDriver(AdamantApiWrapper api, ChatsStorage chatStorage) {
        AdamantWalletFacade driver = new AdamantWalletFacade(api);
        driver.setChatStorage(chatStorage);

        return driver;
    }

    //TODO: Don't forget inject ChatStorage
    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.ETH)
    @Singleton
    @Provides
    public static WalletFacade provideEthereumInfoDriver() {
        return new EthereumWalletFacade();
    }

    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.BNB)
    @Singleton
    @Provides
    public static WalletFacade provideBinanceInfoDriver() {
        return new BinanceWalletFacade();
    }

    @Singleton
    @Provides
    public static QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }

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
    @ContributesAndroidInjector(modules = {SaveSettingsServiceModule.class})
    public abstract SaveSettingsService createSaveSettingsService();


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
            AccountInteractor accountInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            @Named("main") CompositeDisposable compositeDisposable
    ){
        return new MainPresenter(router, accountInteractor, refreshChatsInteractor, compositeDisposable);
    }

}
