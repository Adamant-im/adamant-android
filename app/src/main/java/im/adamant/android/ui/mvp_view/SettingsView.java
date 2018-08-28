package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.core.entities.ServerNode;

public interface SettingsView extends MvpView {
    void clearNodeTextField();
    void hideKeyboard();
    void setStoreKeyPairOption(boolean value);
    void setEnablePushOption(boolean value);
    void setAddressPushService(String address);

    @StateStrategyType(SkipStrategy.class)
    void callSaveKeyPairService(boolean value);
}
