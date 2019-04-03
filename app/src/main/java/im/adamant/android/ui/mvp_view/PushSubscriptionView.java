package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;
import java.util.Map;

import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;

public interface PushSubscriptionView extends MvpView {
    void setEnablePushServiceTypeOption(boolean value);
    void displayCurrentNotificationFacade(PushNotificationServiceFacade facade);
    void startProgress();
    void stopProgress();

    @StateStrategyType(SkipStrategy.class)
    void showSelectServiceDialog(List<PushNotificationServiceFacade> facades, PushNotificationServiceFacade current);

    void showFacadesDescriptions(List<PushNotificationServiceFacade> facades);

    @StateStrategyType(SkipStrategy.class)
    void showMessage(String message);

    @StateStrategyType(SkipStrategy.class)
    void showMessage(int messageResource);
}
