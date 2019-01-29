package im.adamant.android.markdown;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import im.adamant.android.dagger.AppComponent;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.markdown.renderers.block.BlockDescription;
import im.adamant.android.markdown.renderers.block.NewLineBlockRenderer;
import im.adamant.android.markdown.renderers.block.ParagraphBlockRenderer;
import im.adamant.android.markdown.renderers.block.QuoteBlockRenderer;
import im.adamant.android.markdown.renderers.inline.AdamantLinkRenderer;
import im.adamant.android.markdown.renderers.inline.AllowedOtherLinkRenderer;
import im.adamant.android.markdown.renderers.inline.BoldRenderer;
import im.adamant.android.markdown.renderers.inline.EmailLinkRenderer;
import im.adamant.android.markdown.renderers.inline.NewLineRenderer;

public class MarkdownTest {
    @Test
    public void testRNParagraphs() throws Exception {
        String message = "Test paragraph!\r\n\r\nTestParagraph\r\n";

        AdamantMarkdownProcessor markdownProcessor = provideAdamantAddressProcessor();

        String htmlString = markdownProcessor.getHtmlString(message);

        Assert.assertEquals("Test paragraph!<br/><br/>TestParagraph", htmlString);
    }

    @Test
    public void testParagraphsWithBlockquote() throws Exception {
        String message = "Test paragraph!\n\n> TestParagraph\n\n";

        AdamantMarkdownProcessor markdownProcessor = provideAdamantAddressProcessor();

        String htmlString = markdownProcessor.getHtmlString(message);

        Assert.assertEquals("Test paragraph!<br/><br/><blockquote>TestParagraph</blockquote>", htmlString);
    }

    @Test
    public void testThreeNewLine() throws Exception {
        String message = "Test paragraph!\n\n\n TestParagraph\n\n";

        AdamantMarkdownProcessor markdownProcessor = provideAdamantAddressProcessor();

        String htmlString = markdownProcessor.getHtmlString(message);

        Assert.assertEquals("Test paragraph!<br/><br/> TestParagraph", htmlString);
    }


    public AdamantMarkdownProcessor provideAdamantAddressProcessor() {
        AdamantMarkdownProcessor processor = new AdamantMarkdownProcessor();

        // The order of registration is very important.
        processor.registerBlockRenderer(new QuoteBlockRenderer());

        processor.registerInlineRenderer(new AllowedOtherLinkRenderer());
        processor.registerInlineRenderer(new AdamantLinkRenderer());
        processor.registerInlineRenderer(new NewLineRenderer());
        processor.registerInlineRenderer(new EmailLinkRenderer());
        processor.registerInlineRenderer(new BoldRenderer());

        return processor;
    }
}
