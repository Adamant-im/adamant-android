package im.adamant.android.dagger.activities;

import android.os.Build;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.transformations.PassphraseAvatarOutlineProvider;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;

import static org.mockito.Mockito.mock;

@Module
public class TestRegistrationScreenModule {

    @ActivityScope
    @Provides
    public static PassphraseAdapter provideNewPassphraseAdapter() {
        return mock(PassphraseAdapter.class);
    }

    @ActivityScope
    @Provides
    public static PassphraseAvatarTransformation provideAvatarTransforamtion() {
        return mock(PassphraseAvatarTransformation.class);
    }
}
