package im.adamant.android.dagger;

import android.content.Context;

import im.adamant.android.AdamantApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        AvatarsModule.class,
        EncryptionModule.class,
        MessagesModule.class,
        AdamantApiModule.class,
        WalletsModule.class,
        GeneralModule.class,
        MarkdownModule.class,
        InteractorsModule.class,
        PushNotificationsModule.class,
        PresentersModule.class,
        AppModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);
        AppComponent build();
    }

    void inject(AdamantApplication app);
}
