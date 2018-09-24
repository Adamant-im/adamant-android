package im.adamant.android.currencies;

import java.math.BigDecimal;

public interface CurrencyInfoDriver {
    BigDecimal getBalance();
    String getAddress();
    SupportedCurrencyType getCurrencyType();
    //TODO: Remove hardcoded values
    String getTitle();
    int getPrecision();
    int getBackgroundLogoResource();
}
