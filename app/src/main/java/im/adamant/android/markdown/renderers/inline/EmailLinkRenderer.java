package im.adamant.android.markdown.renderers.inline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;

public class EmailLinkRenderer implements InlineRenderer {
    public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+(@)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+(?![-_])");

    @Override
    public Pattern providePattern() {
        return PATTERN;
    }

    @Override
    public void renderItem(StringBuilder builder, Matcher matcher) {
        builder.append("<a href=\"mailto:");
        builder.append(matcher.group(0));
        builder.append("\">");
        builder.append(matcher.group(0));
        builder.append("</a>");
    }
}
