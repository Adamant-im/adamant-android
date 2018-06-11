package im.adamant.android.dagger;

import im.adamant.android.Screens;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.presenters.LoginPresenter;

import javax.inject.Named;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class LoginScreenModule {

    @ActivityScope
    @Provides
    public LoginPresenter provideLoginPresenter(
            Router router,
            AuthorizeInteractor interactor,
            @Named(Screens.LOGIN_SCREEN) CompositeDisposable subscriptions
    ){
        return new LoginPresenter(router,interactor,subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.LOGIN_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
