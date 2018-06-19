package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import java.math.BigDecimal;

public interface WalletView extends MvpView {
    void displayAdamantAddress(String address);
    void displayAdamantBalance(BigDecimal balance);
    void displayShowFreeTokenPageButton();
    void showFreeTokenPage(String address);
    void showJoinIcoPage(String address);
}
