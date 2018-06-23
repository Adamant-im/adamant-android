package im.adamant.android.helpers;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings {
    private static final String NODES_KEY = "nodes_key";

    private List<String> nodes = new ArrayList<>();
    private SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;

        nodes.addAll(
            preferences
                .getStringSet(
                    NODES_KEY,
                    getDefaultNodes()
                )
        );
    }

    public void addNode(String node) {
        nodes.add(node);
        updateNodes();
    }

    public void removeNode(String node) {
        if (nodes.contains(node)){
            nodes.remove(node);
            updateNodes();
        }
    }

    public List<String> getNodes() {
        return nodes;
    }

    private void updateNodes() {
        Set<String> set = new HashSet<>(nodes);
        preferences
                .edit()
                .putStringSet(NODES_KEY, set)
                .apply();
    }

    private Set<String> getDefaultNodes() {
        Set<String> defaults = new HashSet<>();
        defaults.add("https://clown.adamant.im/api/");
        defaults.add("https://lake.adamant.im/api/");
        defaults.add("https://endless.adamant.im/api/");

        return defaults;
    }
}
