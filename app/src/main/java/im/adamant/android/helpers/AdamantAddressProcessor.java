package im.adamant.android.helpers;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdamantAddressProcessor {
    private static final Pattern admLinkPattern = Pattern.compile("U(\\d{15,})(\\?\\S*\\b)*");

    public static class AdamantAddressEntity {
        private String address;
        private String label = "";

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return address + " : " + Objects.toString(label);
        }
    }

    public String getHtmlString(String s) throws Exception {
        Matcher matcher = admLinkPattern.matcher(s);
        StringBuffer buffer = new StringBuffer();
        StringBuilder linkBuilder = new StringBuilder();
        while (matcher.find()){
            linkBuilder.append("<a href=\"adamant://messages?address=U");
            linkBuilder.append(matcher.group(1));

            if (matcher.groupCount() > 1){
                String params = matcher.group(2);
                if (params != null){
                    linkBuilder.append("&");
                    linkBuilder.append(params.substring(1));
                }
            }

            linkBuilder.append("\">");
            linkBuilder.append(matcher.group(1));
            linkBuilder.append("</a>");

            matcher.appendReplacement(buffer, linkBuilder.toString());
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public List<AdamantAddressEntity> extractAdamantAddresses(String s) {
        List<AdamantAddressEntity> addresses = new ArrayList<>();
        Matcher matcher = admLinkPattern.matcher(s);

        while (matcher.find()){
            String address = matcher.group(1);
            AdamantAddressEntity adamantAddressEntity = new AdamantAddressEntity();

            if (!address.startsWith("U")){
                address = "U" + address;
            }

            adamantAddressEntity.setAddress(address);

            if (matcher.groupCount() > 1){
                String paramsString = matcher.group(2);
                if (paramsString != null){
                    Uri parsed = Uri.parse(paramsString);
                    String label = parsed.getQueryParameter("label");
                    if (label != null){
                        adamantAddressEntity.setLabel(label);
                    }
                }
            }

            if (adamantAddressEntity.getLabel().isEmpty()){
                adamantAddressEntity.setLabel(address);
            }

            addresses.add(adamantAddressEntity);
        }

        return addresses;
    }
}
