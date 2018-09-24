package im.adamant.android.currencies;

import java.math.BigDecimal;

public interface CurrencyInfoDriver {
    BigDecimal getBalance();
    String getAddress();
    String getCurrencyType();
    String getTitle();
    int getPrecision();
}
