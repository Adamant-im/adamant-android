package im.adamant.android.dagger;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.helpers.KvsHelper;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;

@Module
public abstract class EncryptionModule {

    @Singleton
    @Provides
    public static JsonParser provideJsonParser() {
        return new JsonParser();
    }

    @Singleton
    @Provides
    public static KeyStoreCipher provideKeyStoreCipher(LazySodium sodium, JsonParser parser, Context context) {
        return new KeyStoreCipher(sodium, parser, context);
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
    public static KvsHelper provideKvsHelper(Encryptor encryptor, AdamantApiWrapper api, Gson gson) {
        return new KvsHelper(api, encryptor, gson);
    }

    @Singleton
    @Provides
    public static ApiKvsProvider provideApiKvsProvider(AdamantApiWrapper adamantApiWrapper) {
        return new ApiKvsProvider(adamantApiWrapper);
    }
}
