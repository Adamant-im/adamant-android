package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface PincodeView extends MvpView {
    void setSuggestionResource(int resourceId);

    @StateStrategyType(SkipStrategy.class)
    void close();

    @StateStrategyType(SkipStrategy.class)
    void showMessage(int stringResourceId);
}
