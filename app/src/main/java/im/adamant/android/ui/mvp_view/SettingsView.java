package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.core.entities.ServerNode;

public interface SettingsView extends MvpView {
    String IS_SAVE_KEYPAIR = "is_save_keypair_key";
    String IS_RECEIVE_NOTIFICATIONS = "is_recieve_notifications_key";

    void setStoreKeyPairOption(boolean value);
    void setEnablePushOption(boolean value);
    void switchPushOption(boolean value);
}
