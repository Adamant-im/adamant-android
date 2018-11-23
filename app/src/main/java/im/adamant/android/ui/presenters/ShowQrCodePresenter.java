package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.ui.mvp_view.ShowQrCodeView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class ShowQrCodePresenter extends BasePresenter<ShowQrCodeView> {

    public ShowQrCodePresenter(CompositeDisposable subscriptions) {
        super(subscriptions);
    }
}
