package im.adamant.android.markdown.renderers.inline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;

public class StrikeRenderer implements InlineRenderer {
    public static final Pattern PATTERN = Pattern.compile("~([^*]+)~");

    @Override
    public Pattern providePattern() {
        return PATTERN;
    }

    @Override
    public void renderItem(StringBuilder itemBuilder, Matcher matcher) {
        itemBuilder.append("<strike>");
        itemBuilder.append(matcher.group(1));
        itemBuilder.append("</strike>");
    }
}
