package com.dremanovich.adamant_android.core.helpers;

import com.dremanovich.adamant_android.core.helpers.interfaces.ServerSelector;

import java.util.ArrayList;
import java.util.List;

public class NaiveServerSelectorImpl implements ServerSelector {
    private List<String> servers = new ArrayList<>();

    public NaiveServerSelectorImpl() {
        servers.add("https://clown.adamant.im/api/");
        servers.add("https://lake.adamant.im/api/");
        servers.add("https://endless.adamant.im/api/");
    }

    @Override
    public String select() {
        int index =  (int) Math.round(Math.floor(Math.random() * servers.size()));
        if (index >= servers.size()){index = servers.size() - 1;}

        return servers.get(index);
    }
}
