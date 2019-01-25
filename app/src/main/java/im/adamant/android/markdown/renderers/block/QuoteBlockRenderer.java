package im.adamant.android.markdown.renderers.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.BlockRenderer;

public class QuoteBlockRenderer implements BlockRenderer {
    public static final Pattern PATTERN = Pattern.compile("^> (.*)");

    @Override
    public BlockDescription getNextBlock(StringBuilder s) {
        Matcher matcher = PATTERN.matcher(s);
        if (matcher.find()) {
            return new BlockDescription(matcher.group(1), matcher.group().length()) ;
        } else {
            return null;
        }
    }

    @Override
    public String renderBlock(String s) {
        return "<blockquote>" + s + "</blockquote>";
    }
}
