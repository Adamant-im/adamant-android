package im.adamant.android.presenters;

import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.R;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.interactors.SettingsInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private SettingsInteractor interactor;

    public SettingsPresenter(SettingsInteractor interactor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.interactor = interactor;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);
        getViewState().setStoreKeyPairOption(
                interactor.isKeyPairMustBeStored()
        );
    }

    public void onClickAddNewNode(String nodeUrl) {
        if (URLUtil.isValidUrl(nodeUrl)){
            interactor.addServerNode(nodeUrl);
            getViewState().clearNodeTextField();
            getViewState().hideKeyboard();
        }
    }

    public void onClickDeleteNode(ServerNode serverNode){
        interactor.deleteNode(serverNode);
    }

    public void onSaveKeyPair(boolean value) {
        if (interactor.isKeyPairMustBeStored() != value) {
            getViewState().callSaveKeyPairService(value);
        }
    }
}
