package com.dremanovich.adamant_android.dagger;

import android.app.Application;
import android.content.Context;

import com.dremanovich.adamant_android.AdamantApplication;
import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.encryption.Encryptor;
import com.dremanovich.adamant_android.core.encryption.KeyGenerator;
import com.dremanovich.adamant_android.core.helpers.NaiveAuthorizationStorageImpl;
import com.dremanovich.adamant_android.core.helpers.NaivePublicKeyStorageImpl;
import com.dremanovich.adamant_android.core.helpers.NaiveServerSelectorImpl;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.helpers.interfaces.PublicKeyStorage;
import com.dremanovich.adamant_android.core.helpers.interfaces.ServerSelector;
import com.dremanovich.adamant_android.interactors.AuthorizeInteractor;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.ui.ChatsScreen;
import com.dremanovich.adamant_android.ui.CreateChatScreen;
import com.dremanovich.adamant_android.ui.LoginScreen;
import com.dremanovich.adamant_android.ui.MessagesScreen;
import com.dremanovich.adamant_android.ui.mappers.LocalizedMessageMapper;
import com.dremanovich.adamant_android.ui.mappers.TransactionToChatMapper;
import com.dremanovich.adamant_android.ui.mappers.TransactionToMessageMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;

import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

@Module(includes = {AndroidSupportInjectionModule.class})
public abstract class AppModule {

    @Singleton
    @Provides
    public static ServerSelector provideServerSelector() {
        return new NaiveServerSelectorImpl();
    }

    @Singleton
    @Provides
    public static AuthorizationStorage provideAuthorizationStorage() {
        return new NaiveAuthorizationStorageImpl();
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
            AuthorizationStorage authorizationStorage
    ) {
        return new TransactionToMessageMapper(encryptor, publicKeyStorage, authorizationStorage);
    }

    @Singleton
    @Provides
    public static TransactionToChatMapper providesTransactionsToChatMapper(AuthorizationStorage authorizationStorage) {
        return new TransactionToChatMapper(authorizationStorage);
    }

    @Singleton
    @Provides
    public static LocalizedMessageMapper providesLocalizedMessageMapper(Context ctx) {
        return new LocalizedMessageMapper(ctx);
    }

    @Singleton
    @Provides
    public static AdamantApi provideApi(ServerSelector serverSelector) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverSelector.select())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(AdamantApi.class);
    }

    @Singleton
    @Provides
    public static PublicKeyStorage providePublicKeyStorage(AdamantApi api) {
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
            AdamantApi api,
            AuthorizationStorage storage,
            KeyGenerator keyGenerator
    ) {
        return new AuthorizeInteractor(api, storage, keyGenerator);
    }

    @Singleton
    @Provides
    public static ChatsInteractor provideChatsInteractor(
            AdamantApi api,
            AuthorizationStorage storage,
            TransactionToMessageMapper messageMapper,
            TransactionToChatMapper chatMapper,
            LocalizedMessageMapper localizedMessageMapper,
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage
    ){
        return new ChatsInteractor(
                api,
                storage,
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
    @ContributesAndroidInjector(modules = {ChatsScreenModule.class})
    public abstract ChatsScreen chatsScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MessagesScreenModule.class})
    public abstract MessagesScreen messagesScreenInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {CreateChatScreenModule.class})
    public abstract CreateChatScreen createChatScreenInjector();
}
