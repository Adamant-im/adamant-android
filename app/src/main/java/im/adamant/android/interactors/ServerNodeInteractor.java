package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.helpers.Settings;

public class ServerNodeInteractor {
    private Settings settings;
    private AdamantApiWrapper api;

    public ServerNodeInteractor(AdamantApiWrapper api, Settings settings) {
        this.settings = settings;
        this.api = api;
    }

    public void addServerNode(String nodeUrl){
        settings.addNode(new ServerNode(nodeUrl));
    }

    public void deleteNode(ServerNode node){settings.removeNode(node);}

    public void switchNode(int index) {
        api.buildApibyIndex(index);
    }

    public void resetToDefaults() {
        settings.resetNodesToDefault();
        api.buildApibyIndex(0);
    }
}
