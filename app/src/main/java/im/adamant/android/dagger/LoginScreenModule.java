package im.adamant.android.dagger;

import dagger.android.ContributesAndroidInjector;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.presenters.LoginPresenter;

import javax.inject.Named;
import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.fragments.BottomLoginFragment;
import im.adamant.android.ui.fragments.BottomNavigationDrawerFragment;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public abstract class LoginScreenModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = {BottomLoginModule.class})
    public abstract BottomLoginFragment provideBottomFragment();
}
