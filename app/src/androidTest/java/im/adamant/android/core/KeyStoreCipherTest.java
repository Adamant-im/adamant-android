package im.adamant.android.core;

import com.google.gson.JsonParser;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import im.adamant.android.InstrumentedTestConstants;
import im.adamant.android.core.encryption.KeyStoreCipher;

public class KeyStoreCipherTest {
    private KeyStoreCipher cipher;

    @Before
    public void setUp() {
        SodiumAndroid sodium = new SodiumAndroid();
        LazySodiumAndroid lazySodiumAndroid = new LazySodiumAndroid(sodium);
        cipher = new KeyStoreCipher(
                lazySodiumAndroid,
                new JsonParser(),
                InstrumentationRegistry.getInstrumentation().getContext()
        );
    }

    @Test
    public void secureHashTest() throws Exception {
        String hash = cipher.secureHash("1212111111");
        Assert.assertNotNull(hash);
        Assert.assertTrue(cipher.verifyHash(hash, "1212111111"));
        Assert.assertFalse(cipher.verifyHash(hash, "1212111112"));
    }

    @Test
    public void encryptAndDecryptSmallBlockTest() throws Exception {
        String testString = "Small data";
        String encryptedString = cipher.encrypt(InstrumentedTestConstants.ADAMANT_TEST_ALIAS, testString);

        Assert.assertNotNull(encryptedString);
        Assert.assertEquals("SD", encryptedString.substring(0,2));

        CharSequence decryptedString = cipher.decrypt(InstrumentedTestConstants.ADAMANT_TEST_ALIAS, encryptedString);
        Assert.assertEquals(testString, decryptedString);

    }

    @Test
    public void encryptAndDecryptLargeBlockTest() throws Exception {
        String testString = "С другой стороны начало повседневной работы по формированию позиции влечет за собой процесс внедрения и модернизации дальнейших направлений развития. Идейные соображения высшего порядка, а также начало повседневной работы по формированию позиции обеспечивает широкому кругу (специалистов) участие в формировании соответствующий условий активизации. Товарищи! укрепление и развитие структуры требуют определения и уточнения дальнейших направлений развития. Равным образом сложившаяся структура организации позволяет выполнять важные задания по разработке системы обучения кадров, соответствует насущным потребностям. Не следует, однако забывать, что постоянный количественный рост и сфера нашей активности представляет собой интересный эксперимент проверки систем массового участия. Повседневная практика показывает, что дальнейшее развитие различных форм деятельности способствует подготовки и реализации систем массового участия.\n" +
                "\n" +
                "Задача организации, в особенности же дальнейшее развитие различных форм деятельности требуют определения и уточнения соответствующий условий активизации. Задача организации, в особенности же постоянное информационно-пропагандистское обеспечение нашей деятельности позволяет оценить значение направлений прогрессивного развития. Идейные соображения высшего порядка, а также дальнейшее развитие различных форм деятельности в значительной степени обуславливает создание системы обучения кадров, соответствует насущным потребностям. Значимость этих проблем настолько очевидна, что начало повседневной работы по формированию позиции способствует подготовки и реализации модели развития. Равным образом новая модель организационной деятельности представляет собой интересный эксперимент проверки модели развития.\n" +
                "\n" +
                "Задача организации, в особенности же сложившаяся структура организации обеспечивает широкому кругу (специалистов) участие в формировании модели развития. Идейные соображения высшего порядка, а также консультация с широким активом позволяет выполнять важные задания по разработке системы обучения кадров, соответствует насущным потребностям.";
        String encryptedString = cipher.encrypt(InstrumentedTestConstants.ADAMANT_TEST_ALIAS, testString);

        Assert.assertNotNull(encryptedString);
        Assert.assertEquals("LD", encryptedString.substring(0,2));

        CharSequence decryptedString = cipher.decrypt(InstrumentedTestConstants.ADAMANT_TEST_ALIAS, encryptedString);
        Assert.assertEquals(testString, decryptedString);

    }
}
