package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface PinCodeView extends MvpView {
    String ARG_MODE = "mode";

    enum MODE {
        ACCESS_TO_APP,
        CREATE,
        CONFIRM
    }

    @StateStrategyType(SkipStrategy.class)
    void startProcess();

    @StateStrategyType(SkipStrategy.class)
    void stopProcess(boolean success);

    void setSuggestion(int resourceId);

//    void shuffleKeyboard();

    @StateStrategyType(SkipStrategy.class)
    void goToMain();

    @StateStrategyType(SkipStrategy.class)
    void goToSplash();

    @StateStrategyType(SkipStrategy.class)
    void close();

    @StateStrategyType(SkipStrategy.class)
    void showError(int resourceId);

    @StateStrategyType(SkipStrategy.class)
    void showRepeatableError(int resourceId, int secondsLeft);

    void setCancelButtonText(int resourceId);

    void clearError();
}
