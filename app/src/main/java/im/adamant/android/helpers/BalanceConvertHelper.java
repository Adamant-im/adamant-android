package im.adamant.android.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BalanceConvertHelper {
    public static final BigDecimal MULTIPLIER = new BigDecimal(100_000_000L);

    public static BigDecimal convert(long balance) {
        return (new BigDecimal(balance))
                .divide(
                        MULTIPLIER,
                        3,
                        RoundingMode.HALF_EVEN
                );
    }
}
