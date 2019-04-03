package im.adamant.android.dagger.services;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class SaveContactsServiceModule {
    public static final String NAME = "SaveContactsServiceModule";

    @ServiceScope
    @Provides
    @Named(NAME)
    public static CompositeDisposable provideDisposable() {
        return new CompositeDisposable();
    }
}
