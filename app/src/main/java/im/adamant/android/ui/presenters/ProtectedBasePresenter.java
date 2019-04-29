package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.MvpView;

import im.adamant.android.Screens;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

public class ProtectedBasePresenter<V extends MvpView> extends BasePresenter<V> {
    protected Router router;
    protected AccountInteractor accountInteractor;

    public ProtectedBasePresenter(Router router, AccountInteractor accountInteractor) {
        this.router = router;
        this.accountInteractor = accountInteractor;
    }

    @Override
    public void attachView(V view) {
        super.attachView(view);

        if (!accountInteractor.isAuthorized()) {
            router.navigateTo(Screens.SPLASH_SCREEN);
        }
    }
}
