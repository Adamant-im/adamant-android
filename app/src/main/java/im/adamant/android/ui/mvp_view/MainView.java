package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

public interface MainView extends MvpView {
    void showWalletScreen();
    void showChatsScreen();
    void showSettingsScreen();
}
