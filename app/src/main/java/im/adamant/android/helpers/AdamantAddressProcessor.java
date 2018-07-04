package im.adamant.android.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdamantAddressProcessor {
    private static final Pattern admLinkPattern = Pattern.compile("U(\\d{15,})(\\?\\S*\\b)*");

    public String getHtmlString(String s) throws Exception {
        Matcher matcher = admLinkPattern.matcher(s);
        StringBuffer buffer = new StringBuffer();
        StringBuilder linkBuilder = new StringBuilder();
        while (matcher.find()){
            linkBuilder.append("<a href=\"adamant://messages?address=");
            linkBuilder.append(matcher.group(0));

            if (matcher.groupCount() > 1){
                linkBuilder.append(matcher.group(1).substring(1));
            }

            linkBuilder.append("\">");
            linkBuilder.append(matcher.group(0));
            linkBuilder.append("</a>");

            matcher.appendReplacement(buffer, linkBuilder.toString());
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public List<String> extractAdamantAddreses(String s) {
        List<String> addreses = new ArrayList<>();
        Matcher matcher = admLinkPattern.matcher(s);

        while (matcher.find()){
            addreses.add(matcher.group(0));
        }

        return addreses;
    }
}
