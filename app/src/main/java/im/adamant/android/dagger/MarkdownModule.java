package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.markdown.AdamantAddressExtractor;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.markdown.renderers.block.QuoteBlockRenderer;
import im.adamant.android.markdown.renderers.inline.AdamantLinkRenderer;
import im.adamant.android.markdown.renderers.inline.AllowedOtherLinkRenderer;
import im.adamant.android.markdown.renderers.inline.BoldRenderer;
import im.adamant.android.markdown.renderers.inline.EmailLinkRenderer;
import im.adamant.android.markdown.renderers.inline.ItalicRenderer;
import im.adamant.android.markdown.renderers.inline.NewLineRenderer;
import im.adamant.android.markdown.renderers.inline.StrikeRenderer;

@Module
public abstract class MarkdownModule {
    @Singleton
    @Provides
    public static AdamantMarkdownProcessor provideAdamantAddressProcessor() {
        AdamantMarkdownProcessor processor = new AdamantMarkdownProcessor();

        processor.registerBlockRenderer(new QuoteBlockRenderer());

        processor.registerInlineRenderer(new AllowedOtherLinkRenderer());
        processor.registerInlineRenderer(new AdamantLinkRenderer());
        processor.registerInlineRenderer(new NewLineRenderer());
        processor.registerInlineRenderer(new EmailLinkRenderer());
        processor.registerInlineRenderer(new BoldRenderer());
        processor.registerInlineRenderer(new ItalicRenderer());
        processor.registerInlineRenderer(new StrikeRenderer());

        return processor;
    }

    @Singleton
    @Provides
    public static AdamantAddressExtractor provideAdamantAddressExtractor() {
        return new AdamantAddressExtractor();
    }
}
