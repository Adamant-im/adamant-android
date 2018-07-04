package im.adamant.android.dagger;

import im.adamant.android.Screens;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.presenters.CreateChatPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class CreateChatScreenModule {
    @ActivityScope
    @Provides
    public CreateChatPresenter provideLoginPresenter(
            Router router,
            ChatsInteractor interactor,
            AdamantAddressProcessor addressProcessor,
            @Named(Screens.CREATE_CHAT_SCREEN) CompositeDisposable subscriptions
    ){
        return new CreateChatPresenter(router,interactor, addressProcessor, subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }
}
