package im.adamant.android.markdown.renderers.block;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.markdown.renderers.BlockRenderer;

public class ParagraphBlockRenderer implements BlockRenderer {

    @Override
    public String getContentBlock(String s) {
        return null;
    }

    @Override
    public String renderBlock(String s) {
        return null;
    }
//    public static final Pattern PATTERN = Pattern.compile("^([^\\n]+(\\n[^\\n]+)*)");
//
//    @Override
//    public BlockDescription getNextBlock(StringBuilder s) {
//        Matcher matcher = PATTERN.matcher(s);
//        if (matcher.find()) {
//            return new BlockDescription(matcher.group(), matcher.group().length());
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public String renderBlock(String s) {
//        return "<p>" + s + "</p>";
//    }
}
