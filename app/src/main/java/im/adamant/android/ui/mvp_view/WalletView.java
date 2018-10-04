package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.math.BigDecimal;
import java.util.List;

import im.adamant.android.currencies.CurrencyTransferEntity;
import im.adamant.android.ui.entities.CurrencyCardItem;

public interface WalletView extends MvpView {
    void showCurrencyCards(List<CurrencyCardItem> currencyCardItems);
    void showLastTransfers(List<CurrencyTransferEntity> currencyTransferEntities);

    @StateStrategyType(SkipStrategy.class)
    void putAddressToClipboard(String address);

    @StateStrategyType(SkipStrategy.class)
    void createQrCode(String address);
}
