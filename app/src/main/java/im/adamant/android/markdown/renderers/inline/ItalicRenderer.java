package im.adamant.android.markdown.renderers.inline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;

public class ItalicRenderer implements InlineRenderer {
    public static final Pattern PATTERN = Pattern.compile("_([^*]+?)_|\\*([^*]+?)\\*");

    @Override
    public Pattern providePattern() {
        return PATTERN;
    }

    @Override
    public void renderItem(StringBuilder itemBuilder, Matcher matcher) {
        if (matcher.group(2) != null) {
            itemBuilder.append("<i>");
            itemBuilder.append(matcher.group(2));
            itemBuilder.append("</i>");
        } else if (matcher.group(1) != null) {
            itemBuilder.append("<i>");
            itemBuilder.append(matcher.group(1));
            itemBuilder.append("</i>");
        }
    }
}
