package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.core.AdamantApiBuilder;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.helpers.NaivePublicKeyStorageImpl;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.helpers.Settings;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestAdamantApiModule {
    @Singleton
    @Provides
    public static AdamantApiBuilder provideApiBuilder() {
        return mock(AdamantApiBuilder.class);
    }

    @Singleton
    @Provides
    public static AdamantApiWrapper provideAdamantApiWrapper() {
        return mock(AdamantApiWrapper.class);
    }

    @Singleton
    @Provides
    public static PublicKeyStorage providePublicKeyStorage() {
        return mock(NaivePublicKeyStorageImpl.class);
    }
}
