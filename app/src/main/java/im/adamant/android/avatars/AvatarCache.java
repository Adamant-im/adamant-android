package im.adamant.android.avatars;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import im.adamant.android.core.encryption.Hex;

public class AvatarCache {
    private static Map<String, Bitmap> cache = new HashMap<>();

    public void put(String key, int size, Bitmap avatar) {
        String storeKey = Hex.md5Hash(key + size);
        cache.put(storeKey, avatar);
    }

    public Bitmap get(String key, int size) {
        Bitmap bitmap = null;
        String storeKey = Hex.md5Hash(key + size);
        if (cache.containsKey(storeKey)){
            bitmap = cache.get(storeKey);
        }
        return bitmap;
    }
}
