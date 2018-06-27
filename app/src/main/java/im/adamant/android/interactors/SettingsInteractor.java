package im.adamant.android.interactors;

import im.adamant.android.helpers.Settings;
import im.adamant.android.rx.ObservableRxList;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.Single;

public class SettingsInteractor {
    private Settings settings;

    public SettingsInteractor(
            Settings settings
    ) {
        this.settings = settings;
    }

    public void addServerNode(String nodeUrl){
        settings.addNode(new ServerNode(nodeUrl));
    }

    public void deleteNode(ServerNode node){settings.removeNode(node);}
}
