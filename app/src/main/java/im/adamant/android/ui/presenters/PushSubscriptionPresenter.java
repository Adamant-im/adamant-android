package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.ui.mvp_view.PushSubscriptionView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class PushSubscriptionPresenter extends BasePresenter<PushSubscriptionView> {

    public PushSubscriptionPresenter(CompositeDisposable subscriptions) {
        super(subscriptions);
    }

}
