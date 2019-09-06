package im.adamant.android.cases.markdown;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import im.adamant.android.TestApplication;
import im.adamant.android.markdown.AdamantAddressEntity;
import im.adamant.android.markdown.AdamantAddressExtractor;
import im.adamant.android.shadows.LocaleChangerShadow;

@RunWith(RobolectricTestRunner.class)
@Config(
        application = TestApplication.class,
        sdk = Config.TARGET_SDK,
        manifest = Config.NONE,
        shadows = {LocaleChangerShadow.class}
)
public class AdamantAddressExtractorTest {

    @Test
    public void extractValidAddress() {
        String addressString = "address adm:U17493488006346417000?label=Label address";

        AdamantAddressExtractor extractor = new AdamantAddressExtractor();

        List<AdamantAddressEntity> adamantAddressEntities = extractor.extractAdamantAddresses(addressString);
        Assert.assertNotNull(adamantAddressEntities);
        Assert.assertTrue(adamantAddressEntities.size() > 0);

        AdamantAddressEntity adamantAddressEntity = adamantAddressEntities.get(0);
        Assert.assertNotNull(adamantAddressEntity);

        Assert.assertEquals("U17493488006346417000", adamantAddressEntity.getAddress());
        Assert.assertEquals("Label", adamantAddressEntity.getLabel());
    }

    @Test
    public void extractShortAddressMustBeFailed() {
        String addressString = "address adm:U17493488999999?label=Label address";

        AdamantAddressExtractor extractor = new AdamantAddressExtractor();

        List<AdamantAddressEntity> adamantAddressEntities = extractor.extractAdamantAddresses(addressString);
        Assert.assertNotNull(adamantAddressEntities);
        Assert.assertEquals(0, adamantAddressEntities.size());
    }
}
