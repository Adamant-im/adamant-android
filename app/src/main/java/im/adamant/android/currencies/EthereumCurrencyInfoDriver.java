package im.adamant.android.currencies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import im.adamant.android.R;
import im.adamant.android.helpers.ChatsStorage;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class EthereumCurrencyInfoDriver implements CurrencyInfoDriver {
    @Override
    public BigDecimal getBalance() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAddress() {
        return "Coming soon";
    }

    @Override
    public SupportedCurrencyType getCurrencyType() {
        return SupportedCurrencyType.ETH;
    }

    @Override
    public String getTitle() {
        return "ETHEREUM WALLET";
    }

    @Override
    public int getPrecision() {
        return 8;
    }

    @Override
    public int getBackgroundLogoResource() {
        return R.drawable.ic_ethereum_line;
    }

    @Override
    public void setChatStorage(ChatsStorage chatStorage) {

    }

    @Override
    public Single<List<CurrencyTransferEntity>> getLastTransfers() {
        return Single.just(new ArrayList<>());
    }
}
