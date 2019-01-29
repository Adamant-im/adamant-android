package im.adamant.android.markdown;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.markdown.renderers.BlockRenderer;
import im.adamant.android.markdown.renderers.InlineRenderer;
import im.adamant.android.markdown.renderers.block.BlockDescription;

public class AdamantMarkdownProcessor {
    private static final String PARAGRAPH_SEPSRATOR = "<br/><br/>";
//    public static final Pattern PARAGRAPH_PATTERN = Pattern.compile("^([^\\n]+(\\n[^\\n]+)*)");
    public static final Pattern FILTER_EMPTY_LINES_PATTERN = Pattern.compile("\\n{3,}?", Pattern.MULTILINE);

    private Set<InlineRenderer> inlineRenderers = new LinkedHashSet<>();
    private Set<BlockRenderer> blockRenderers = new LinkedHashSet<>();

    public String getHtmlString(String s) throws Exception {
        return processing(s);
    }

    public void registerInlineRenderer(InlineRenderer renderer) {
        inlineRenderers.add(renderer);
    }

    public void registerBlockRenderer(BlockRenderer renderer) {
        blockRenderers.add(renderer);
    }

    private String processing(String s) throws Exception {
        StringBuilder blocks = new StringBuilder();

        s = s
                .trim()
                .replace("\r","");

        s = FILTER_EMPTY_LINES_PATTERN.matcher(s).replaceAll("\n\n");

        String[] paragraphs = s.split("\n\n");

        for (String paragraph : paragraphs) {
            boolean isBlockRendered = false;
            for (BlockRenderer renderer : blockRenderers) {
                String contentBlock = renderer.getContentBlock(paragraph);
                if (!contentBlock.isEmpty()) {
                    paragraph = applyInlineRenderers(escape(contentBlock));
                    paragraph = renderer.renderBlock(paragraph);
                    isBlockRendered = true;
                    break;
                }
            }

            if (!isBlockRendered) {
                paragraph = applyInlineRenderers(escape(paragraph));
            }

            blocks.append(PARAGRAPH_SEPSRATOR);
            blocks.append(paragraph);
        }


        int brLength = PARAGRAPH_SEPSRATOR.length();
        blocks.delete(0, brLength);

        return blocks.toString();

    }

    private String applyInlineRenderers(String s) {
        for (InlineRenderer renderer: inlineRenderers) {
            s = render(renderer, s);
        }
        return s;
    }

    private String render(InlineRenderer renderer, String s) {
        Matcher matcher = renderer.providePattern().matcher(s);
        StringBuffer buffer = new StringBuffer();
        StringBuilder itemBuilder = new StringBuilder();
        while (matcher.find()){
            renderer.renderItem(itemBuilder, matcher);
            matcher.appendReplacement(buffer, itemBuilder.toString());
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c){
                case '&': {
                    sb.append("&amp;");
                }
                break;
                case '<': {
                    sb.append("&lt;");
                }
                break;
                case '>': {
                    sb.append("&gt;");
                }
                break;
                case '"': {
                    sb.append("&quot;");
                }
                break;
                case '\'': {
                    sb.append("&#39;");
                }
                break;
                case '=': {
                    sb.append("&#x3D;");
                }
                break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

}
