package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.TransferDetailsInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.presenters.TransferDetailsPresenter;
import ru.terrakok.cicerone.Router;

@Module
public class TransferDetailsScreenModule {
    @ActivityScope
    @Provides
    public TransferDetailsPresenter provideTransferDetailsPresenter(Router router, AccountInteractor accountInteractor,
                                                                    TransferDetailsInteractor interactor, ChatsStorage chatsStorage) {
        return new TransferDetailsPresenter(router, accountInteractor, interactor, chatsStorage);
    }
}
