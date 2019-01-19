package im.adamant.android.markdown.renderers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdamantLinkRenderer implements Renderer {
    @Override
    public Pattern providePattern() {
        return ADM_LINK;
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
