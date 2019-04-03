package im.adamant.android.markdown.renderers.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import im.adamant.android.markdown.renderers.BlockRenderer;

public class QuoteBlockRenderer implements BlockRenderer {
    public static final Pattern PATTERN = Pattern.compile("^> (.*)");

//    @Override
//    public BlockDescription getNextBlock(StringBuilder s) {
//        Matcher matcher = PATTERN.matcher(s);
//        if (matcher.find()) {
//            return new BlockDescription(matcher.group(1), matcher.group().length()) ;
//        } else {
//            return null;
//        }
//    }

    @NonNull
    @Override
    public String getContentBlock(String s) {
        Matcher matcher = PATTERN.matcher(s);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    @Override
    public String renderBlock(String s) {
        return "<blockquote>" + s + "</blockquote>";
    }
}
