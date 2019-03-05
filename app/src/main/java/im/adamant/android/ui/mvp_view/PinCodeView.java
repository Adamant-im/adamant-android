package im.adamant.android.ui.mvp_view;

import android.os.Bundle;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface PinCodeView extends MvpView {
    String ARG_VERIFIED = "verified";
    String ARG_CREATED = "created";

    enum MODE {
        VERIFY,
        CREATE
    }

    void setSuggestion(int resourceId);

    @StateStrategyType(SkipStrategy.class)
    void close(Bundle bundle);

    @StateStrategyType(SkipStrategy.class)
    void showError(int resourceId);
}
