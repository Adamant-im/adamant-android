package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.avatars.AvatarGraphics;
import im.adamant.android.avatars.AvatarThemesProvider;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestAvatarsModule {
    @Singleton
    @Provides
    public static AvatarThemesProvider provideAvatarThemes() {
        return mock(AvatarThemesProvider.class);
    }

    @Singleton
    @Provides
    public static AvatarGraphics provideAvatarGraphics() {
        return mock(AvatarGraphics.class);
    }

    @Singleton
    @Provides
    public static Avatar provideAvatar(){
        return mock(Avatar.class);
    }
}
