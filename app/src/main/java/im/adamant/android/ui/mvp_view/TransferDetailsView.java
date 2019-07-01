package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import im.adamant.android.ui.entities.TransferDetails;

public interface TransferDetailsView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showTransferDetails(TransferDetails details);
}
