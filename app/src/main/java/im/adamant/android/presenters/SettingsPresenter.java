package im.adamant.android.presenters;

import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private SaveKeypairInteractor saveKeypairInteractor;
    private SubscribeToPushInteractor subscribeToPushInteractor;

    public SettingsPresenter(
            SaveKeypairInteractor saveKeypairInteractor,
            SubscribeToPushInteractor subscribeToPushInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.saveKeypairInteractor = saveKeypairInteractor;
        this.subscribeToPushInteractor = subscribeToPushInteractor;
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
            saveKeypairInteractor.addServerNode(nodeUrl);
            getViewState().clearNodeTextField();
            getViewState().hideKeyboard();
        }
    }


    public void onClickSaveSettings(){
        getViewState().callSaveSettingsService();
    }

//    public void onSavePushConfig(boolean enable, String address, C) {
//        subscribeToPushInteractor.savePushConfig(enable, address);
//    }

    public void onClickDeleteNode(ServerNode serverNode){
        saveKeypairInteractor.deleteNode(serverNode);
    }

//    public void onSaveKeyPair(boolean value) {
//        if (saveKeypairInteractor.isKeyPairMustBeStored() != value) {
//            getViewState().callSaveKeyPairService(value);
//        }
//    }
}
