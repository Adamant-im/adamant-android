package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.helpers.Settings;
import im.adamant.android.ui.adapters.ServerNodeAdapter;

import static org.mockito.Mockito.mock;

@Module
public class TestNodesListScreenModule {
    @ActivityScope
    @Provides
    public ServerNodeAdapter provideAdapter() {
        return mock(ServerNodeAdapter.class);
    }
}
