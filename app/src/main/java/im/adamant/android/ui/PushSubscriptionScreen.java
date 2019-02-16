package im.adamant.android.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
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

    @BindView(R.id.activity_push_subscription_tv_current_service)
    TextView currentServiceView;

    @BindView(R.id.activity_push_subscription_pb_progress)
    ProgressBar progressBarView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_push_subscribtion_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.activity_push_subscription_title));
    }

    @Override
    public void setEnablePushServiceTypeOption(boolean value) {
        currentServiceView.setEnabled(value);
    }

    @Override
    public void displayCurrentNotificationFacade(PushNotificationServiceFacade facade) {
        currentServiceView.setText(getString(facade.getShortTitleResource()));
    }

    @Override
    public void startProgress() {
        progressBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgress() {
        progressBarView.setVisibility(View.GONE);
    }
}
