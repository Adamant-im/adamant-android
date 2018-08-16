package im.adamant.android.dagger;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.SaveContactsInteractor;
import im.adamant.android.presenters.CompanionDetailPresenter;
import im.adamant.android.rx.ChatsStorage;
import ru.terrakok.cicerone.Router;

@Module
public class CompanionDetailScreenModule {
    @ActivityScope
    @Provides
    public CompanionDetailPresenter provideCompanionDetailPresenter(
            Router router,
            ChatsStorage chatsStorage
    ){
        return new CompanionDetailPresenter(
                router,
                chatsStorage
        );
    }
}
