package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.ui.entities.CurrencyTransferEntity;

public interface AllTransactionsView extends MvpView {
    void firstTransfersWasLoaded(List<CurrencyTransferEntity> transfers);
    void newTransferWasLoaded(CurrencyTransferEntity transfer);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setLoading(boolean loading);

    void nextTransferWasLoaded(CurrencyTransferEntity transfer);
}
