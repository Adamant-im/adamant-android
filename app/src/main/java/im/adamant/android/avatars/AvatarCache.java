package im.adamant.android.avatars;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import im.adamant.android.core.encryption.Hex;

public class AvatarCache {
    private static Map<String, Bitmap> cache = new HashMap<>();

    public void put(String key, float size, Bitmap avatar) {
        String sizeStr = String.format(Locale.ENGLISH, "%.5f", size);
        String storeKey = Hex.md5Hash(key + sizeStr);
        cache.put(storeKey, avatar);
    }

    public Bitmap get(String key, float size) {
        Bitmap bitmap = null;
        String sizeStr = String.format(Locale.ENGLISH, "%.5f", size);
        String storeKey = Hex.md5Hash(key + sizeStr);
        if (cache.containsKey(storeKey)){
            bitmap = cache.get(storeKey);
        }
        return bitmap;
    }
}
