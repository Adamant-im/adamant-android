package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.ServerNodeInteractor;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import im.adamant.android.ui.presenters.NodesListPresenter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class NodesListScreenModule {
    @ActivityScope
    @Provides
    public static NodesListPresenter provideNodesListPresenter(
            ServerNodeInteractor serverNodeInteractor,
            @Named(Screens.NODES_LIST_SCREEN) CompositeDisposable subscriptions
    ) {
        return new NodesListPresenter(
                serverNodeInteractor,
                subscriptions
        );
    }

    @ActivityScope
    @Provides
    @Named(Screens.NODES_LIST_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    public ServerNodeAdapter provideAdapter(Settings settings){
        return new ServerNodeAdapter(settings.getNodes());
    }
}
