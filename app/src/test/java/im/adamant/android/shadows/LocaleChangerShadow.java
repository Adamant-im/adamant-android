package im.adamant.android.shadows;

import android.content.Context;

import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.LocalePreference;
import com.franmontiel.localechanger.matcher.MatchingAlgorithm;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.util.ReflectionHelpers;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;


@Implements(LocaleChanger.class)
public class LocaleChangerShadow {
    private static AtomicInteger calls = new AtomicInteger(0);

    @Implementation
    public static void initialize(Context context,
                                  List<Locale> supportedLocales,
                                  MatchingAlgorithm matchingAlgorithm,
                                  LocalePreference preference) {
        //Block Multiple Initialization Error
        if (calls.get() == 0) {
            calls.compareAndSet(0, 1);
            Shadow.directlyOn(
                    LocaleChanger.class,
                    "initialize",
                    ReflectionHelpers.ClassParameter.from(Context.class, context),
                    ReflectionHelpers.ClassParameter.from(List.class, supportedLocales),
                    ReflectionHelpers.ClassParameter.from(MatchingAlgorithm.class, matchingAlgorithm),
                    ReflectionHelpers.ClassParameter.from(LocalePreference.class, preference)
            );
        }
    }
}
