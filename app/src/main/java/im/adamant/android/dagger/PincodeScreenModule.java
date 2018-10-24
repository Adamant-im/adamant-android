package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.interactors.KeypairInteractor;
import im.adamant.android.presenters.PincodePresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class PincodeScreenModule {
    @ActivityScope
    @Provides
    public PincodePresenter providePincodePresenter(
            Router router,
            KeypairInteractor keypairInteractor,
            @Named(Screens.PINCODE_SCREEN) CompositeDisposable subscriptions
    ){
        return new PincodePresenter(router, keypairInteractor, subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.PINCODE_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
