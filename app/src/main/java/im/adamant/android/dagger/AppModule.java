package im.adamant.android.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.encryption.KeyGenerator;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.NaivePublicKeyStorageImpl;
import im.adamant.android.helpers.Settings;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.interactors.SettingsInteractor;
import im.adamant.android.services.ServerNodesPingService;
import im.adamant.android.ui.CreateChatScreen;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.MessagesScreen;
import im.adamant.android.ui.ScanQrCodeScreen;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
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
    public static Settings provideSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return new Settings(preferences);
    }

//    @Singleton
//    @Provides
//    public static ServerSelector provideServerSelector(Settings settings) {
//        return new NaiveServerSelectorImpl(settings);
//    }

//    @Singleton
//    @Provides
//    public static AuthorizationStorage provideAuthorizationStorage() {
//        return new NaiveAuthorizationStorageImpl();
//    }

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
    public static KeyGenerator providesKeyGenerator(SeedCalculator seedCalculator, MnemonicGenerator mnemonicGenerator, LazySodium sodium) {
        return new KeyGenerator(seedCalculator, mnemonicGenerator, sodium);
    }

    @Singleton
    @Provides
    public static Encryptor providesMessageEncryptor(LazySodium sodium) {
        return new Encryptor(sodium);
    }

    @Singleton
    @Provides
    public static TransactionToMessageMapper providesTransactionsToMessageMapper(
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage,
            AdamantApiWrapper api
    ) {
        return new TransactionToMessageMapper(encryptor, publicKeyStorage, api);
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
    public static AdamantApiWrapper provideAdamantApiWrapper(Settings settings, KeyGenerator keyGenerator) {
        return new AdamantApiWrapper(settings.getNodes(), keyGenerator);
    }

//    @Singleton
//    @Provides
//    public static AdamantApi provideApi(ServerSelector serverSelector) {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(logging);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(serverSelector.select())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(httpClient.build())
//                .build();
//
//        return retrofit.create(AdamantApi.class);
//    }

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
            KeyGenerator keyGenerator
    ) {
        return new AuthorizeInteractor(api, keyGenerator);
    }

    @Singleton
    @Provides
    public static AccountInteractor provideAccountInteractor(
            AdamantApiWrapper api
    ) {
        return new AccountInteractor(api);
    }

    @Singleton
    @Provides
    public static SettingsInteractor provideSettingsInteractor(
            Settings settings
    ) {
        return new SettingsInteractor(settings);
    }

    @Singleton
    @Provides
    public static AdamantAddressProcessor provideAdamantAddressProcessor() {
        return new AdamantAddressProcessor();
    }

    @Singleton
    @Provides
    public static ChatsInteractor provideChatsInteractor(
            AdamantApiWrapper api,
            TransactionToMessageMapper messageMapper,
            TransactionToChatMapper chatMapper,
            LocalizedMessageMapper localizedMessageMapper,
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage
    ){
        return new ChatsInteractor(
                api,
                messageMapper,
                chatMapper,
                localizedMessageMapper,
                encryptor,
                publicKeyStorage
        );
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


    //--Services

    @ServiceScope
    @ContributesAndroidInjector(modules = {ServerNodePingServiceModule.class})
    public abstract ServerNodesPingService createServerNodePingService();
}
