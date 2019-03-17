package im.adamant.android.ui.mvp_view;

import android.os.Bundle;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface PinCodeView extends MvpView {
    int PINCODE_LENGTH = 10;
    String ARG_MODE = "mode";

    enum MODE {
        ACCESS_TO_APP,
        CREATE,
        CONFIRM,
        DROP
    }

    void startProcess();
    void stopProcess();

    void setSuggestion(int resourceId);

    void dropPincodeText();

    @StateStrategyType(SkipStrategy.class)
    void goToMain();

    @StateStrategyType(SkipStrategy.class)
    void close();

    @StateStrategyType(SkipStrategy.class)
    void showError(int resourceId);
}
