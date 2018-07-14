package im.adamant.android.helpers;

public interface AdamantKeyValueStorage {
    <T> void put(String key, T value, boolean encrypt);
    <T> T get(String key);
    <T> T get(String key, String ownerPublicKey);
}
