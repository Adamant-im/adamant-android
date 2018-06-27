package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.interactors.SettingsInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
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

    }

    public void onClickAddNewNode(String nodeUrl) {
        interactor.addServerNode(nodeUrl);
    }
}
