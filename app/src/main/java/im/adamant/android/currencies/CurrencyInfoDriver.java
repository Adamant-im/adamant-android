package im.adamant.android.currencies;

import java.math.BigDecimal;

public interface CurrencyInfoDriver {
    BigDecimal getBalance();
    String getAddress();
    String getCurrencyType();
    //TODO: Remove hardcoded values
    String getTitle();
    int getPrecision();
}
