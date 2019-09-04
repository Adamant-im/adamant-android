package im.adamant.android.dagger;

import com.google.gson.Gson;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiBuilder;
import im.adamant.android.core.DefaultAdamantApiBuilderImpl;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.helpers.PublicKeyStorageImpl;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.helpers.Settings;
import io.reactivex.Scheduler;

@Module
public abstract class AdamantApiModule {

    @Singleton
    @Provides
    public static AdamantApiBuilder provideApiBuilder(Settings settings, Gson gson) {
        return new DefaultAdamantApiBuilderImpl(settings.getNodes(), gson);
    }

    @Singleton
    @Provides
    public static AdamantApiWrapper provideAdamantApiWrapper(
            AdamantApiBuilder apiBuilder,
            AdamantKeyGenerator keyGenerator,
            @Named(Constants.IO_SCHEDULER) Scheduler scheduler
    ) {
        return new AdamantApiWrapper(apiBuilder, keyGenerator, scheduler);
    }

    @Singleton
    @Provides
    public static PublicKeyStorage providePublicKeyStorage(AdamantApiWrapper api) {
        return new PublicKeyStorageImpl(api);
    }
}
