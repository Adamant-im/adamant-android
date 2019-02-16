package im.adamant.android.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
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

    @OnClick(R.id.activity_push_subscription_tv_current_service)
    public void onClickSelectPushService() {
        presenter.showSelectServiceDialog();
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
        progressBarView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSelectServiceDialog(
            List<PushNotificationServiceFacade> facades,
            PushNotificationServiceFacade current
    ) {
        androidx.appcompat.app.AlertDialog.Builder builder = null;

        builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.activity_push_subscription_select_type_title));

        CharSequence[] titles = new CharSequence[facades.size()];

        int defaultSelected = 0;
        SupportedPushNotificationFacadeType currentFacadeType = current.getFacadeType();
        for (int i = 0; i < titles.length; i++){
            titles[i] = getString(facades.get(i).getTitleResource());

            if (currentFacadeType.equals(facades.get(i).getFacadeType())){
                defaultSelected = i;
            }
        }

        AtomicInteger selectedLangIndex = new AtomicInteger(defaultSelected);

        builder.setSingleChoiceItems(titles, defaultSelected, (d, i) -> {
            selectedLangIndex.set(i);
        });

        int finalDefaultSelected = defaultSelected;
        builder.setPositiveButton(R.string.yes, (d, i) -> {
            int currentSelected = selectedLangIndex.get();
            if (finalDefaultSelected != currentSelected){
                presenter.onClickSetNewPushService(facades.get(currentSelected));
            }
        });
        builder.setNegativeButton(R.string.no, null);

        builder.show();

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(int messageResource) {
        Toast.makeText(this, messageResource, Toast.LENGTH_LONG).show();
    }
}
