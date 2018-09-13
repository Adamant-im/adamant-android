package im.adamant.android.avatars;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class AvatarCache {
    private static Map<String, Bitmap> cache = new HashMap<>();

    public void put(String key, Bitmap avatar) {
        cache.put(key, avatar);
    }

    public Bitmap get(String key) {
        Bitmap bitmap = null;
        if (cache.containsKey(key)){
            bitmap = cache.get(key);
        }
        return bitmap;
    }
}
