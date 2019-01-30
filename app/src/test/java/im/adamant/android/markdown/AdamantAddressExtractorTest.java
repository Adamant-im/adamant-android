package im.adamant.android.markdown;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class AdamantAddressExtractorTest {

    //TODO: Robolectric test
    @Ignore
    @Test
    public void extractValidAddress() {
        String addressString = "address adm:U17493488006346417000?label=Label address";

        AdamantAddressExtractor extractor = new AdamantAddressExtractor();

        List<AdamantAddressEntity> adamantAddressEntities = extractor.extractAdamantAddresses(addressString);
        Assert.assertNotNull(adamantAddressEntities);

        AdamantAddressEntity adamantAddressEntity = adamantAddressEntities.get(0);
        Assert.assertNotNull(adamantAddressEntity);

        Assert.assertEquals("U17493488006346417000", adamantAddressEntity.getAddress());
        Assert.assertEquals("Label", adamantAddressEntity.getLabel());
    }
}
