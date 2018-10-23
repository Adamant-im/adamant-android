package im.adamant.android.presenters;

import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;
import com.google.firebase.iid.FirebaseInstanceId;

import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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

    public void onClickSaveSettings(boolean enable, String address){
        subscribeToPushInteractor.savePushConfig(enable, address);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            Disposable subscribe = subscribeToPushInteractor
                    .savePushToken(deviceToken)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError((error) -> LoggerHelper.e("savePushToken", error.getMessage(), error))
                    .subscribe();
            subscriptions.add(subscribe);
        });
    }

    public void onClickDeleteNode(ServerNode serverNode){
        serverNodeInteractor.deleteNode(serverNode);
    }

    public void onClickDropSavedKeyPair() {
        saveKeypairInteractor.dropKeyPair();
    }
}
