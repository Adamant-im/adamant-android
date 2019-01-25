package im.adamant.android.markdown;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import im.adamant.android.markdown.renderers.BlockRenderer;
import im.adamant.android.markdown.renderers.InlineRenderer;
import im.adamant.android.markdown.renderers.block.BlockDescription;

public class AdamantMarkdownProcessor {
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
        StringBuilder originalString = new StringBuilder(s);

        boolean isCorrect = false;
        do {

            for (BlockRenderer blockRenderer : blockRenderers) {
                BlockDescription description = blockRenderer.getNextBlock(originalString);
                boolean isFound = (description != null);
                isCorrect |= isFound;

                if (isFound) {
                    String block = description.getContent();
                    originalString = originalString.delete(0, description.getLenghtInOriginal());

                    if (block.trim().isEmpty()) {
                        continue;
                    }

                    block = escape(block);

                    for (InlineRenderer inlineRenderer : inlineRenderers) {
                        block = render(inlineRenderer, block);
                    }

                    blocks.append(blockRenderer.renderBlock(block));
                }
            }

            //TODO: Perhaps in the case of syntax corruption, you should not ignore the damaged part, but throw the exception
            if (!isCorrect) {
                break;
            }

        } while (originalString.length() > 0);

        return blocks.toString();

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
