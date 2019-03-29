package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.adapters.KeyPinAdapter;

@Module
public class PincodeScreenModule {

    @ActivityScope
    @Provides
    public static KeyPinAdapter provideKeyPinAdapter() {
        return new KeyPinAdapter();
    }
}
