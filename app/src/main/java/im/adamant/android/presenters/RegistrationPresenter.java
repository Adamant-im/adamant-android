package im.adamant.android.presenters;

import im.adamant.android.ui.mvp_view.RegistrationView;
import io.reactivex.disposables.CompositeDisposable;

public class RegistrationPresenter extends BasePresenter<RegistrationView> {

    public RegistrationPresenter(CompositeDisposable subscriptions) {
        super(subscriptions);
    }

}
