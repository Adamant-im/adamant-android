package im.adamant.android.ui;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.PushSubscriptionView;
import im.adamant.android.ui.presenters.PushSubscriptionPresenter;

public class PushSubscriptionScreen extends BaseActivity implements PushSubscriptionView {
    @Inject
    Provider<PushSubscriptionPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    PushSubscriptionPresenter presenter;

    @ProvidePresenter
    public PushSubscriptionPresenter getPresenter(){
        return presenterProvider.get();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_push_subscribtion_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    public void setEnablePushServiceTypeOption(boolean value) {

    }

    @Override
    public void startProgress() {

    }

    @Override
    public void stopProgress() {

    }
}
