package im.adamant.android.core.helpers;

import im.adamant.android.core.helpers.interfaces.ServerSelector;
import im.adamant.android.helpers.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NaiveServerSelectorImpl implements ServerSelector {
    private Settings settings;
    private List<String> servers = new ArrayList<>();

    public NaiveServerSelectorImpl(Settings settings) {
        this.settings = settings;
        servers = Arrays.asList((String[]) settings.getNodes().toArray());

        //Default servers
        if (servers.size() == 0) {
            servers.add("https://clown.adamant.im/api/");
            servers.add("https://lake.adamant.im/api/");
            servers.add("https://endless.adamant.im/api/");
        }

    }

    @Override
    public String select() {
        int index =  (int) Math.round(Math.floor(Math.random() * servers.size()));
        if (index >= servers.size()){index = servers.size() - 1;}

        return servers.get(index);
    }
}