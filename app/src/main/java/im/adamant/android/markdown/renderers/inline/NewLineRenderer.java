package im.adamant.android.markdown.renderers.inline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;

public class NewLineRenderer implements InlineRenderer {
    public static final Pattern PATTERN = Pattern.compile("\\n");

    @Override
    public Pattern providePattern() {
        return PATTERN;
    }

    @Override
    public void renderItem(StringBuilder builder, Matcher matcher) {
        builder.append("<br/>");
    }
}
