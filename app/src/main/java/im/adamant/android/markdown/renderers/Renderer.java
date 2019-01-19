package im.adamant.android.markdown.renderers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Renderer {
    Pattern ADM_LINK = Pattern.compile("U(\\d{15,})(\\?\\S*\\b)*");
    Pattern ALLOWED_LINK = Pattern.compile("((eth|bch|bitcoin|https?|s?ftp|magnet|tor|onion|tg):([^\\s\\x00-\\x1f<>]+[^\\s\\x00-\\x1f<>.]))");
    Pattern NEW_LINE = Pattern.compile("\\n");

    Pattern providePattern();
    void renderItem(StringBuilder builder, Matcher matcher);
}
