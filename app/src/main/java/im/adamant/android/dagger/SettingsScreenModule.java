package im.adamant.android.dagger;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.SettingsInteractor;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.ui.adapters.LanguageAdapter;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class SettingsScreenModule {

    @ActivityScope
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            SettingsInteractor interactor,
            @Named(value = Screens.SETTINGS_SCREEN) CompositeDisposable subscriptions
    ) {
        return new SettingsPresenter(interactor, subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.SETTINGS_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    public ServerNodeAdapter provideAdapter(Settings settings){
        return new ServerNodeAdapter(settings.getNodes());
    }

    @ActivityScope
    @Provides
    public LanguageAdapter provideLanguageAdapter(Context context) {
        List<Locale> list = new ArrayList<>();
        list.add(new Locale("en"));
        list.add(new Locale("ru"));

        return new LanguageAdapter(context, android.R.layout.simple_spinner_item, list);
    }
}
