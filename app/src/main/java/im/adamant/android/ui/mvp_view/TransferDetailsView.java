package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import im.adamant.android.interactors.wallets.entities.TransferDetails;
import im.adamant.android.ui.entities.UITransferDetails;

public interface TransferDetailsView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showTransferDetails(UITransferDetails details);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setLoading(boolean loading);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void openBrowser(String url);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void share(String text);

    void shareStatus(TransferDetails.STATUS status);
}
