package im.adamant.android.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.helpers.NaivePublicKeyStorageImpl;
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
import im.adamant.android.ui.CompanionDetailScreen;
import im.adamant.android.ui.CreateChatScreen;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.MessagesScreen;
import im.adamant.android.ui.ScanQrCodeScreen;
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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;
import im.adamant.android.ui.messages_support.factories.AdamantBasicMessageFactory;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.EthereumTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.FallbackMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;
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
            AdamantApiWrapper api
    ) {
        MessageFactoryProvider provider = new MessageFactoryProvider();

        provider.registerFactory(
                SupportedMessageTypes.ADAMANT_BASIC,
                new AdamantBasicMessageFactory(adamantAddressProcessor, encryptor, api)
        );

        provider.registerFactory(
                SupportedMessageTypes.FALLBACK,
                new FallbackMessageFactory(adamantAddressProcessor)
        );

        provider.registerFactory(
                SupportedMessageTypes.ETHEREUM_TRANSFER,
                new EthereumTransferMessageFactory(adamantAddressProcessor)
        );

        provider.registerFactory(
                SupportedMessageTypes.ADAMANT_SUBSCRIBE_ON_NOTIFICATION,
                new AdamantPushSubscriptionMessageFactory(encryptor, api)
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
    public static TransactionToChatMapper providesTransactionsToChatMapper(AdamantApiWrapper api) {
        return new TransactionToChatMapper(api);
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

    //--Activities

    @ActivityScope
    @ContributesAndroidInjector(modules = {LoginScreenModule.class})
    public abstract LoginScreen loginScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MessagesScreenModule.class})
    public abstract MessagesScreen messagesScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {CreateChatScreenModule.class})
    public abstract CreateChatScreen createChatScreenInjector();

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
    @ContributesAndroidInjector(modules = {CompanionDetailScreenModule.class})
    public abstract CompanionDetailScreen createCompanionDetailInjector();


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
}
