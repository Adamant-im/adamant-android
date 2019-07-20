package im.adamant.android.dagger.activities;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.ui.adapters.KeyPinAdapter;
import im.adamant.android.ui.presenters.PincodePresenter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class PincodeScreenModule {

    @ActivityScope
    @Provides
    public static KeyPinAdapter provideKeyPinAdapter() {
        return new KeyPinAdapter();
    }

    @ActivityScope
    @Provides
    public static PincodePresenter providePincodePresenter(
            SecurityInteractor securityInteractor
    ) {
        return new PincodePresenter(securityInteractor);
    }
}
