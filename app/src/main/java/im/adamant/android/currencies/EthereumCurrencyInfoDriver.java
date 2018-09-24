package im.adamant.android.currencies;

import java.math.BigDecimal;

import im.adamant.android.R;

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
}
