package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import im.adamant.android.Screens;
import im.adamant.android.ui.mvp_view.MainView;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private Router router;

    private String currentWindowCode = Screens.WALLET_SCREEN;

    public MainPresenter(Router router) {
        this.router = router;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        router.navigateTo(currentWindowCode);
    }

    public void onSelectedWalletTab() {
        currentWindowCode = Screens.WALLET_SCREEN;
        router.navigateTo(currentWindowCode);
    }

    public void onSelectedChatsTab() {
        currentWindowCode = Screens.CHATS_SCREEN;
        router.navigateTo(currentWindowCode);
    }

    public void onSelectedSettingsTab() {
        currentWindowCode = Screens.SETTINGS_SCREEN;
        router.navigateTo(currentWindowCode);
    }
}
