package im.adamant.android.dagger;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.presenters.CompanionDetailPresenter;
import im.adamant.android.helpers.ChatsStorage;
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
