package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface CompanionDetailView extends MvpView {
    void showCompanionName(String name);

    @StateStrategyType(SkipStrategy.class)
    void startSavingContacts();
}
