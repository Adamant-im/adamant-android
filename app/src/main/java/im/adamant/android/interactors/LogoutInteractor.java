package im.adamant.android.interactors;

import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.helpers.Settings;
import im.adamant.android.rx.Irrelevant;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class LogoutInteractor {
    private ChatsStorage chatsStorage;
    private Settings settings;
    private AdamantApiWrapper api;
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;

    private PublishSubject<Irrelevant> publisher = PublishSubject.create();
    private Flowable eventBus = publisher.toFlowable(BackpressureStrategy.LATEST);
    private Disposable logoutDisposable;

    public LogoutInteractor(
            ChatsStorage chatsStorage,
            Settings settings,
            AdamantApiWrapper api,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor
    ) {
        this.chatsStorage = chatsStorage;
        this.settings = settings;
        this.api = api;
        this.switchPushNotificationServiceInteractor = switchPushNotificationServiceInteractor;
    }

    public Flowable<Irrelevant> getEventBus() {
        return eventBus;
    }

    public void execute() {
        if (logoutDisposable != null) {
            logoutDisposable.dispose();
        }

        logoutDisposable = switchPushNotificationServiceInteractor
                .resetNotificationFacade(true)
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .subscribe(
                        () -> {
                            chatsStorage.cleanUp();
                            api.logout();
                            settings.setAccountPassphrase("");
                            settings.setKeyPairMustBeStored(false);

                            publisher.onNext(Irrelevant.INSTANCE);
                        },
                        (error) -> {
                            publisher.onError(error);

                        }
                );
    }

    @Override
    protected void finalize() throws Throwable {

        if (logoutDisposable != null){
            logoutDisposable.dispose();
            logoutDisposable = null;
        }
        super.finalize();
    }
}
