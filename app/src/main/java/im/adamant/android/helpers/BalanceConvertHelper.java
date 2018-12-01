package im.adamant.android.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BalanceConvertHelper {
    private static final BigDecimal HUNDRED_MILLION = new BigDecimal(100_000_000L);

    public static BigDecimal convert(long balance) {
        return (new BigDecimal(balance))
                .divide(
                        HUNDRED_MILLION,
                        3,
                        RoundingMode.HALF_EVEN
                );
    }
}
