package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface CreateChatView extends MvpView {
    void showError(int resourceId);
    void lockUI();
    void unlockUI();
    void showQrCode(String content);

    @StateStrategyType(SkipStrategy.class)
    void close();
}
