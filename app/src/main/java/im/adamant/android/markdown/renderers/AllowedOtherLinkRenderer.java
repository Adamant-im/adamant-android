package im.adamant.android.markdown.renderers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllowedOtherLinkRenderer implements Renderer {
    @Override
    public Pattern providePattern() {
        return ALLOWED_LINK;
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
