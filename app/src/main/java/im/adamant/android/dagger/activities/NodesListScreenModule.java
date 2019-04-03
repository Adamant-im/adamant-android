package im.adamant.android.dagger.activities;

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
    public ServerNodeAdapter provideAdapter(Settings settings){
        return new ServerNodeAdapter(settings.getNodes());
    }

    @ActivityScope
    @Provides
    public static NodesListPresenter provideNodesListPresenter(
            ServerNodeInteractor serverNodeInteractor
    ) {
        return new NodesListPresenter(
                serverNodeInteractor
        );
    }
}
