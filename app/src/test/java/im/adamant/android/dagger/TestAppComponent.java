package im.adamant.android.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import im.adamant.android.TestApplication;
import im.adamant.android.cases.services.SaveSettingsServiceTest;

@Singleton
@Component(modules = {
        AvatarsModule.class,
        EncryptionModule.class,
        MessagesModule.class,
        AdamantApiModule.class,
        WalletsModule.class,
        GeneralModule.class,
        MarkdownModule.class,
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
    void inject(SaveSettingsServiceTest test);
}
