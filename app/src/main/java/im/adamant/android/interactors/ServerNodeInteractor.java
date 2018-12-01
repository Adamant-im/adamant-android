package im.adamant.android.interactors;

import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.helpers.Settings;

public class ServerNodeInteractor {
    private Settings settings;

    public ServerNodeInteractor(Settings settings) {
        this.settings = settings;
    }

    public void addServerNode(String nodeUrl){
        settings.addNode(new ServerNode(nodeUrl));
    }

    public void deleteNode(ServerNode node){settings.removeNode(node);}
}
