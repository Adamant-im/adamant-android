package im.adamant.android.presenters;

import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private SaveKeypairInteractor saveKeypairInteractor;
    private SubscribeToPushInteractor subscribeToPushInteractor;
    private ServerNodeInteractor serverNodeInteractor;

    public SettingsPresenter(
            SaveKeypairInteractor saveKeypairInteractor,
            SubscribeToPushInteractor subscribeToPushInteractor,
            ServerNodeInteractor serverNodeInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.saveKeypairInteractor = saveKeypairInteractor;
        this.subscribeToPushInteractor = subscribeToPushInteractor;
        this.serverNodeInteractor = serverNodeInteractor;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);
        getViewState().setStoreKeyPairOption(
                saveKeypairInteractor.isKeyPairMustBeStored()
        );
        getViewState().setEnablePushOption(
                subscribeToPushInteractor.isEnabledPush()
        );
        getViewState().setAddressPushService(
                subscribeToPushInteractor.getPushServiceAddress()
        );
    }

    public void onClickAddNewNode(String nodeUrl) {
        if (URLUtil.isValidUrl(nodeUrl)){
            serverNodeInteractor.addServerNode(nodeUrl);
            getViewState().clearNodeTextField();
            getViewState().hideKeyboard();
        }
    }

    public void onClickSaveSettings(){
        getViewState().callSaveSettingsService();
    }

    public void onClickDeleteNode(ServerNode serverNode){
        serverNodeInteractor.deleteNode(serverNode);
    }
}
