package im.adamant.android.markdown;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import im.adamant.android.markdown.renderers.Renderer;

public class AdamantAddressExtractor {

    public List<AdamantAddressEntity> extractAdamantAddresses(String s) {
        List<AdamantAddressEntity> addresses = new ArrayList<>();
        Matcher matcher = Renderer.ADM_LINK.matcher(s);

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
