package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.SendCurrencyInteractor;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class SendCurrencyTransferFragmentModule {
    @FragmentScope
    @Provides
    public SendCurrencyPresenter provideSendCurrencyPresenter(
            Router router,
            SendCurrencyInteractor sendCurrencyInteractor,
            PublicKeyStorage publicKeyStorage,
            ChatsStorage chatsStorage,
            @Named(Screens.SEND_CURRENCY_TRANSFER_SCREEN) CompositeDisposable subscriptions
    ){
        return new SendCurrencyPresenter(
                router,
                sendCurrencyInteractor,
                publicKeyStorage,
                chatsStorage,
                subscriptions
        );
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.SEND_CURRENCY_TRANSFER_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
