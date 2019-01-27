package im.adamant.android.ui.presenters;

import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private Router router;
    private SaveKeypairInteractor saveKeypairInteractor;
    private SubscribeToPushInteractor subscribeToPushInteractor;

    public SettingsPresenter(
            Router router,
            SaveKeypairInteractor saveKeypairInteractor,
            SubscribeToPushInteractor subscribeToPushInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
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
    }

    public void onClickSaveSettings(){
        getViewState().callSaveSettingsService();
    }

    public void onClickShowNodesList() {
        router.navigateTo(Screens.NODES_LIST_SCREEN);
    }

}
