package im.adamant.android.markdown.renderers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewLineRenderer implements Renderer {
    @Override
    public Pattern providePattern() {
        return NEW_LINE;
    }

    @Override
    public void renderItem(StringBuilder builder, Matcher matcher) {
        builder.append("<br/>");
    }
}
