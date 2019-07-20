package im.adamant.android.ui.presenters;

import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.rx.RxTaskManager;
import im.adamant.android.ui.mvp_view.NodesListView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class NodesListPresenter extends BasePresenter<NodesListView> {
    private ServerNodeInteractor serverNodeInteractor;

    public NodesListPresenter(ServerNodeInteractor serverNodeInteractor) {
        this.serverNodeInteractor = serverNodeInteractor;
    }

    public void onClickAddNewNode(String nodeUrl) {
        if (URLUtil.isValidUrl(nodeUrl)){
            serverNodeInteractor.addServerNode(nodeUrl);
            getViewState().clearNodeTextField();
            getViewState().hideKeyboard();
        }
    }

    public void onClickDeleteNode(ServerNode serverNode){
        serverNodeInteractor.deleteNode(serverNode);
    }

    public void onClickSwitchNode(int index) {
        serverNodeInteractor.switchNode(index);
    }

    public void onClickResetDefaults() {
        serverNodeInteractor.resetToDefaults();
    }
}
