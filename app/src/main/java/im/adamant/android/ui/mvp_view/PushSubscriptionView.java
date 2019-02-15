package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import im.adamant.android.interactors.push.PushNotificationServiceFacade;

public interface PushSubscriptionView extends MvpView {
    void setEnablePushServiceTypeOption(boolean value);
    void displayCurrentNotificationFacade(PushNotificationServiceFacade facade);
    void startProgress();
    void stopProgress();
}
