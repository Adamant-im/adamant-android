package im.adamant.android.dagger.services;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class SaveSettingsServiceModule {
    public static final String NAME = "SaveSettingsServiceModule";

    @ServiceScope
    @Provides
    @Named(NAME)
    public static CompositeDisposable provideDisposable() {
        return new CompositeDisposable();
    }
}
