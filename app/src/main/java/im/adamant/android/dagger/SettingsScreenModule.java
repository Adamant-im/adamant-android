package im.adamant.android.dagger;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.SettingsInteractor;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.adapters.LanguageAdapter;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class SettingsScreenModule {

    @FragmentScope
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            SettingsInteractor interactor,
            @Named(value = Screens.SETTINGS_SCREEN) CompositeDisposable subscriptions
    ) {
        return new SettingsPresenter(interactor, subscriptions);
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
