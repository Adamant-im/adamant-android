package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import java.math.BigDecimal;

public interface WalletView extends MvpView {
    String SHOW_FREE_TOKEN_PAGE = "showFreeToken";

    void displayAdamantAddress(String address);
    void displayAdamantBalance(BigDecimal balance);
    void displayFreeTokenPageButton();
}
