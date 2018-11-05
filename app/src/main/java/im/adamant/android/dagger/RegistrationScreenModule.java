package im.adamant.android.dagger;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.transformations.PassphraseAvatarOutlineProvider;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;

@Module
public class RegistrationScreenModule {

    @ActivityScope
    @Provides
    public static PassphraseAdapter provideNewPassphraseAdapter(
            Avatar avatar,
            PassphraseAvatarOutlineProvider outlineProvider,
            AdamantKeyGenerator keyGenerator
    ) {
        return new PassphraseAdapter(avatar, outlineProvider, keyGenerator);
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
}
