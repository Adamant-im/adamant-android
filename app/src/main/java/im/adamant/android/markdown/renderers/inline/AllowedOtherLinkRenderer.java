package im.adamant.android.markdown.renderers.inline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;

public class AllowedOtherLinkRenderer implements InlineRenderer {
    public static final Pattern PATTERN = Pattern.compile("((eth|bch|bitcoin|https?|s?ftp|magnet|tor|onion|tg):([^\\s\\x00-\\x1f<>]+[^\\s\\x00-\\x1f<>.]))\\b");

    @Override
    public Pattern providePattern() {
        return PATTERN;
    }

    @Override
    public void renderItem(StringBuilder builder, Matcher matcher) {
        builder.append("<a href=\"");
        builder.append(matcher.group(1));
        builder.append("\">");
        builder.append(matcher.group(1));
        builder.append("</a>");
    }
}
