package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.presenters.RegistrationPresenter;
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
            Avatar avatar,
            PassphraseAvatarOutlineProvider outlineProvider
    ) {
        return new PassphraseAdapter(avatar, outlineProvider);
    }

    @ActivityScope
    @Provides
    public static PassphraseAvatarOutlineProvider provideAvatarOutlineProvider() {
        return new PassphraseAvatarOutlineProvider();
    }

    @ActivityScope
    @Provides
    public static PassphraseAvatarTransformation provideAvatarTransforamtion() {
        return new PassphraseAvatarTransformation();
    }

    @ActivityScope
    @Provides
    public static RegistrationPresenter providePresenter(
            Router router,
            AuthorizeInteractor authorizeInteractor,
            @Named(Screens.REGISTRATION_SCREEN) CompositeDisposable subscriptions
    ) {
        return new RegistrationPresenter(router, authorizeInteractor, subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.REGISTRATION_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
