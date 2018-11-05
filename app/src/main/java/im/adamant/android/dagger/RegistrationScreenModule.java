package im.adamant.android.dagger;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.adapters.ViewPagerPassphraseAdapter;

@Module
public class RegistrationScreenModule {

    @ActivityScope
    @Provides
    public static ViewPagerPassphraseAdapter providePassphraseAdapter(Avatar avatar, AdamantKeyGenerator keyGenerator) {
        return new ViewPagerPassphraseAdapter(avatar, keyGenerator);
    }

    @ActivityScope
    @Provides
    public static PassphraseAdapter provideNewPassphraseAdapter(Avatar avatar, AdamantKeyGenerator keyGenerator) {
        return new PassphraseAdapter(avatar, keyGenerator);
    }
}
