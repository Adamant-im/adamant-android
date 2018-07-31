package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class SplashScreenModule {


    @ActivityScope
    @Provides
    @Named(value = Screens.SPLASH_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
