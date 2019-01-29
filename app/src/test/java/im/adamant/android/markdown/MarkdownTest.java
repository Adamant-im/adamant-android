package im.adamant.android.markdown;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;
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

        AdamantMarkdownProcessor markdownProcessor = provideAdamantMarkdownProcessor();

        String htmlString = markdownProcessor.getHtmlString(message);

        Assert.assertEquals("Test paragraph!<br/><br/>TestParagraph", htmlString);
    }

    @Test
    public void testParagraphsWithBlockquote() throws Exception {
        String message = "Test paragraph!\n\n> TestParagraph\n\n";

        AdamantMarkdownProcessor markdownProcessor = provideAdamantMarkdownProcessor();

        String htmlString = markdownProcessor.getHtmlString(message);

        Assert.assertEquals("Test paragraph!<br/><br/><blockquote>TestParagraph</blockquote>", htmlString);
    }

    @Test
    public void testThreeNewLine() throws Exception {
        String message = "Test paragraph!\n\n\n TestParagraph\n\n";

        AdamantMarkdownProcessor markdownProcessor = provideAdamantMarkdownProcessor();

        String htmlString = markdownProcessor.getHtmlString(message);

        Assert.assertEquals("Test paragraph!<br/><br/> TestParagraph", htmlString);
    }

    @Test
    public void testAdamantLink() {
        String message = "sentence (U17493488006346417000?label=Test+User), next sentence";

        AdamantLinkRenderer adamantLinkRenderer = new AdamantLinkRenderer();

        String renderedMessage = render(adamantLinkRenderer, message);

        Assert.assertEquals("sentence (<a href=\"adamant://messages?address=U17493488006346417000&label=Test+User\">17493488006346417000</a>), next sentence", renderedMessage);
    }

    @Test
    public void testOtherLink() {
        String message = "sentence (https://google.com?s=query), next sentence";

        AllowedOtherLinkRenderer otherLinkRenderer = new AllowedOtherLinkRenderer();

        String renderedMessage = render(otherLinkRenderer, message);

        Assert.assertEquals("sentence (<a href=\"https://google.com?s=query\">https://google.com?s=query</a>), next sentence", renderedMessage);
    }

    @Test
    public void testEmailLink() {
        String message = "sentence (test@test.com), next sentence";

        EmailLinkRenderer emailLinkRenderer = new EmailLinkRenderer();

        String renderedMessage = render(emailLinkRenderer, message);

        Assert.assertEquals("sentence (<a href=\"mailto:test@test.com\">test@test.com</a>), next sentence", renderedMessage);
    }

    @Test
    public void testBold() {
        String message = "sentence (*bold*), next sentence";

        BoldRenderer bold = new BoldRenderer();

        String renderedMessage = render(bold, message);

        Assert.assertEquals("sentence (<b>bold</b>), next sentence", renderedMessage);
    }


    private String render(InlineRenderer renderer, String s) {
        Matcher matcher = renderer.providePattern().matcher(s);
        StringBuffer buffer = new StringBuffer();
        StringBuilder itemBuilder = new StringBuilder();
        while (matcher.find()){
            renderer.renderItem(itemBuilder, matcher);
            matcher.appendReplacement(buffer, itemBuilder.toString());
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private AdamantMarkdownProcessor provideAdamantMarkdownProcessor() {
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
