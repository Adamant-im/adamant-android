package im.adamant.android.markdown;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;

import im.adamant.android.markdown.renderers.Renderer;

public class AdamantMarkdownProcessor {
    private Set<Renderer> renderers = new LinkedHashSet<>();

    public String getHtmlString(String s) throws Exception {
        s = escape(s);
        return processing(s);
    }

    public void registerRenderer(Renderer renderer) {
        renderers.add(renderer);
    }

    private String processing(String s) throws Exception {
        for (Renderer renderer : renderers) {
            Matcher matcher = renderer.providePattern().matcher(s);
            StringBuffer buffer = new StringBuffer();
            StringBuilder itemBuilder = new StringBuilder();
            while (matcher.find()){
                renderer.renderItem(itemBuilder, matcher);
                matcher.appendReplacement(buffer, itemBuilder.toString());
            }

            matcher.appendTail(buffer);

            s = buffer.toString();
        }

        return s;
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
