package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import im.adamant.android.Screens;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.SendMessageInteractor;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private Router router;
    private MessageFactoryProvider messageFactoryProvider;
    private SendMessageInteractor sendMessageInteractor;
    private Settings settings;
    private CompositeDisposable compositeDisposable;

    private String currentWindowCode = Screens.WALLET_SCREEN;

    public MainPresenter(
            Router router,
            Settings settings,
            MessageFactoryProvider messageFactoryProvider,
            SendMessageInteractor sendMessageInteractor,
            CompositeDisposable compositeDisposable
    ) {
        this.router = router;
        this.compositeDisposable = compositeDisposable;
        this.messageFactoryProvider = messageFactoryProvider;
        this.sendMessageInteractor = sendMessageInteractor;
        this.settings = settings;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        router.navigateTo(currentWindowCode);
    }

    @Override
    public void detachView(MainView view) {
        super.detachView(view);
        compositeDisposable.dispose();
        compositeDisposable.clear();
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

    public void onGetNotificationToken(String deviceToken) {

    }
}
