package im.adamant.android.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import im.adamant.android.TestApplication;
import im.adamant.android.cases.presenters.SettingsPresenterTest;

@Singleton
@Component(modules = {
        TestAvatarsModule.class,
        TestEncryptionModule.class,
        TestMessagesModule.class,
        TestAdamantApiModule.class,
        TestWalletsModule.class,
        TestGeneralModule.class,
        TestMarkdownModule.class,
        TestInteractorsModule.class,
        AppModule.class
})
public interface TestAppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);
        TestAppComponent build();
    }

    void inject(TestApplication app);
    void inject(SettingsPresenterTest test);
}
