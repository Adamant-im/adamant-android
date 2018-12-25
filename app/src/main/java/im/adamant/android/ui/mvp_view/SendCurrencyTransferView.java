package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.math.BigDecimal;
import java.util.List;

import im.adamant.android.ui.entities.SendCurrencyEntity;

public interface SendCurrencyTransferView extends MvpView {
    void setTransferIsSupported(boolean value);
    void setRecipientAddress(String address);
    void setFee(BigDecimal fee, String currencyAbbr);
    void setCurrentBalance(BigDecimal balance, String currencyAbbr);
    void setReminder(BigDecimal reminder, String currencyAbbr);
    void setTotalAmount(BigDecimal totalAmount, String currencyAbbr);
    void setRecipientName(String name);
    void lockSendButton();
    void unlockSendButton();
    void setEditTextIcons(int resourceId);
    void hideCommentField();
    void showCommentField();

    @StateStrategyType(SkipStrategy.class)
    void showTransferConfirmationDialog(BigDecimal amount, String currencyAbbr, String address);
}
