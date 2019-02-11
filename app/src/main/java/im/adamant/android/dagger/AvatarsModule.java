package im.adamant.android.dagger;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.avatars.AvatarGraphics;
import im.adamant.android.avatars.AvatarThemesProvider;
import im.adamant.android.avatars.CachedAvatar;
import im.adamant.android.avatars.RoundWithBorderAvatar;
import im.adamant.android.avatars.SquareAvatar;

@Module
public abstract class AvatarsModule {
    @Singleton
    @Provides
    public static AvatarThemesProvider provideAvatarThemes() {
        return new AvatarThemesProvider();
    }

    @Singleton
    @Provides
    public static AvatarGraphics provideAvatarGraphics(AvatarThemesProvider avatarThemesProvider) {
        return new AvatarGraphics(avatarThemesProvider);
    }

    @Singleton
    @Provides
    public static Avatar provideAvatar(Context context, AvatarGraphics graphics){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int borderSizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1.0f,
                displayMetrics
        );

        int paddingSizePx = (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8.0f,
                displayMetrics
        );

        return new CachedAvatar(
                new RoundWithBorderAvatar(
                        new SquareAvatar(graphics),
                        paddingSizePx,
                        borderSizePx
                ),
                1024 * 1024 * 10 // 10Mb
        );
    }

}
