package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class EncryptKeyPairServiceModule {
    public static final String NAME = "EncryptKeyPairServiceModule";

    @ServiceScope
    @Provides
    @Named(NAME)
    public static CompositeDisposable provideDisposable() {
        return new CompositeDisposable();
    }
}
