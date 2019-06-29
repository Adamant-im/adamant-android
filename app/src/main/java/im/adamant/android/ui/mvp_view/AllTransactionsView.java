package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import im.adamant.android.ui.entities.CurrencyTransferEntity;

public interface AllTransactionsView extends MvpView {
    void firstTransfersWasLoaded(List<CurrencyTransferEntity> transfers);
    void newTransferWasLoaded(CurrencyTransferEntity transfer);

    void setLoading(boolean loading);
}
