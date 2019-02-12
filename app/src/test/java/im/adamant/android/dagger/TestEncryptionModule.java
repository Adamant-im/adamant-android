package im.adamant.android.dagger;

import android.content.Context;

import com.google.gson.Gson;
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

import static org.mockito.Mockito.mock;

@Module
public abstract class TestEncryptionModule {
    @Singleton
    @Provides
    public static KeyStoreCipher provideKeyStoreCipher() {
        return mock(KeyStoreCipher.class);
    }

    @Singleton
    @Provides
    public static SeedCalculator provideSeedCalculator() {
        return mock(SeedCalculator.class);
    }

    @Singleton
    @Provides
    public static MnemonicGenerator provideMnemonic(){
        return mock(MnemonicGenerator.class);
    }

    @Singleton
    @Provides
    public static LazySodium provideLazySodium() {
        return mock(LazySodiumAndroid.class);
    }

    @Singleton
    @Provides
    public static AdamantKeyGenerator providesKeyGenerator() {
        return mock(AdamantKeyGenerator.class);
    }

    @Singleton
    @Provides
    public static Encryptor providesMessageEncryptor() {
        return mock(Encryptor.class);
    }

    @Singleton
    @Provides
    public static KvsHelper provideKvsHelper() {
        return mock(KvsHelper.class);
    }

    @Singleton
    @Provides
    public static ApiKvsProvider provideApiKvsProvider() {
        return mock(ApiKvsProvider.class);
    }
}
