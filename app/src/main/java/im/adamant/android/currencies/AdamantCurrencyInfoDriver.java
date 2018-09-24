package im.adamant.android.currencies;

import java.math.BigDecimal;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.BalanceConvertHelper;

public class AdamantCurrencyInfoDriver implements CurrencyInfoDriver {
    private AdamantApiWrapper api;

    public AdamantCurrencyInfoDriver(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public BigDecimal getBalance() {
        if (api.isAuthorized()){
            return BalanceConvertHelper.convert(api.getAccount().getUnconfirmedBalance());
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getAddress() {
        String address = "";
        if (api.isAuthorized()){
            address = api.getAccount().getAddress();
        }

        return address;
    }

    @Override
    public String getCurrencyType() {
        return SupportedCurrencyType.ADM;
    }

    @Override
    public String getTitle() {
        return "ADAMANT WALLET";
    }

    @Override
    public int getPrecision() {
        return 3;
    }
}
