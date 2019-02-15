package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

public interface PushSubscriptionView extends MvpView {
    void setEnablePushServiceTypeOption(boolean value);
    //TODO: установка текущего типа уведолений
    void startProgress();
    void stopProgress();
}
