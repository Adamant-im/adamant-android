package im.adamant.android.dagger.activities;

import android.os.Build;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.presenters.RegistrationPresenter;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.transformations.PassphraseAvatarOutlineProvider;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class RegistrationScreenModule {

    @ActivityScope
    @Provides
    public static PassphraseAdapter provideNewPassphraseAdapter(
            Avatar avatar
    ) {
        PassphraseAdapter passphraseAdapter = new PassphraseAdapter(avatar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            passphraseAdapter.setOutlineProvider(new PassphraseAvatarOutlineProvider());
        }

        return passphraseAdapter;
    }


    @ActivityScope
    @Provides
    public static PassphraseAvatarTransformation provideAvatarTransforamtion() {
        return new PassphraseAvatarTransformation();
    }

    @ActivityScope
    @Provides
    public static RegistrationPresenter provideRegistrationPresenter(
            Router router,
            AuthorizeInteractor authorizeInteractor
    ) {
        return new RegistrationPresenter(router, authorizeInteractor);
    }
}
