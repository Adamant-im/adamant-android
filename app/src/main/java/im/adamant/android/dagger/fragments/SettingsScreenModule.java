package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.ui.presenters.SettingsPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class SettingsScreenModule {
    @FragmentScope
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            Router router,
            SaveKeypairInteractor saveKeypairInteractor,
            SubscribeToPushInteractor subscribeToPushInteractor,
            @Named(Screens.SETTINGS_SCREEN) CompositeDisposable subscriptions
    ) {
        return new SettingsPresenter(
                router,
                saveKeypairInteractor,
                subscribeToPushInteractor,
                subscriptions
        );
    }

    @FragmentScope
    @Provides
    @Named(Screens.SETTINGS_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
