package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.core.entities.ServerNode;

public interface SettingsView extends MvpView {
    String IS_SAVE_KEYPAIR = "is_save_keypair_key";
    String IS_RECEIVE_NOTIFICATIONS = "is_receive_notifications_key";

    void setCheckedStoreKeyPairOption(boolean value);
    void setEnableStoreKeyPairOption(boolean value);
    void setEnablePushOption(boolean value);
    void setCheckedPushOption(boolean value);

    void showSaveSettingsButton(boolean value);
}
