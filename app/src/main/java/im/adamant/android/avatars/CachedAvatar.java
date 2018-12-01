package im.adamant.android.avatars;

import android.graphics.Bitmap;
import android.util.LruCache;

import io.reactivex.Single;

public class CachedAvatar implements Avatar {
    private final LruCache<String, Bitmap> cache;
    private Avatar delegate;

    public CachedAvatar(Avatar delegate, int cacheSize) {
        this.delegate = delegate;
        cache = new LruCache<>(cacheSize);
    }

    @Override
    public Single<Bitmap> build(String key, int sizePx) {
        String hashKey = key + sizePx;

        Bitmap bitmap = cache.get(hashKey);
        if (bitmap != null){return Single.just(bitmap);}

        return delegate
                .build(key, sizePx)
                .doOnSuccess(avatar -> cache.put(hashKey, avatar));
    }

    @Override
    public AvatarGraphics getGraphics() {
        return null;
    }
}
