package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.markdown.AdamantAddressExtractor;
import im.adamant.android.markdown.AdamantMarkdownProcessor;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestMarkdownModule {
    @Singleton
    @Provides
    public static AdamantMarkdownProcessor provideAdamantAddressProcessor() {
        return mock(AdamantMarkdownProcessor.class);
    }

    @Singleton
    @Provides
    public static AdamantAddressExtractor provideAdamantAddressExtractor() {
        return mock(AdamantAddressExtractor.class);
    }
}
