package im.adamant.android.markdown.renderers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface InlineRenderer {
    Pattern providePattern();
    void renderItem(StringBuilder itemBuilder, Matcher matcher);
}
