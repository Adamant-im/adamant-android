package im.adamant.android.markdown.renderers.inline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.InlineRenderer;


public class AdamantLinkRenderer implements InlineRenderer {
   public static final Pattern PATTERN = Pattern.compile("U(\\d{15,})(\\?\\S*\\b)*");

    @Override
    public Pattern providePattern() {
        return PATTERN;
    }

    @Override
    public void renderItem(StringBuilder builder, Matcher matcher) {
        builder.append("<a href=\"adamant://messages?address=U");
        builder.append(matcher.group(1));

        if (matcher.groupCount() > 1){
            String params = matcher.group(2);
            if (params != null){
                builder.append("&");
                builder.append(params.substring(1));
            }
        }

        builder.append("\">");
        builder.append(matcher.group(1));
        builder.append("</a>");
    }


}
