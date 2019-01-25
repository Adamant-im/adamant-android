package im.adamant.android.ui.messages_support.entities;

import android.content.Context;

import java.math.BigDecimal;
import im.adamant.android.R;

public class AdamantTransferMessage extends AbstractMessage {
    private BigDecimal amount;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
        String message = "";
        if (isiSay()) {
            message = context.getString(R.string.sended);
        } else {
            message = context.getString(R.string.received);
        }

        message += amount + context.getString(R.string.adm_currency_abbr);

        return message;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
