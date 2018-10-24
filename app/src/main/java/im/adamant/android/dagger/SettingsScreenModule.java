package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.KeypairInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class SettingsScreenModule {

    @FragmentScope
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            KeypairInteractor keypairInteractor,
            SubscribeToPushInteractor subscribeToPushInteractor,
            ServerNodeInteractor serverNodeInteractor,
            @Named(value = Screens.SETTINGS_SCREEN) CompositeDisposable subscriptions
    ) {
        return new SettingsPresenter(
                keypairInteractor,
                subscribeToPushInteractor,
                serverNodeInteractor,
                subscriptions
        );
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.SETTINGS_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @FragmentScope
    @Provides
    public ServerNodeAdapter provideAdapter(Settings settings){
        return new ServerNodeAdapter(settings.getNodes());
    }
}
