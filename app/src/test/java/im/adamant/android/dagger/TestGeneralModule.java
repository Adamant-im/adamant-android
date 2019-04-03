package im.adamant.android.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Constants;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.helpers.Settings;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestGeneralModule {
    @Singleton
    @Provides
    public static Gson provideGson() {
        return mock(Gson.class);
    }

    @Singleton
    @Provides
    public static GsonBuilder provideGsonBuilder() {
        return mock(GsonBuilder.class);
    }

    @Singleton
    @Provides
    public static List<Locale> provideSupportedLocale() {
        Locale ru = new Locale("ru");
        Locale en = new Locale("en");

        return Arrays.asList(en, ru);
    }

    @Singleton
    @Provides
    public static Settings provideSettings() {
        return mock(Settings.class);
    }

    @Singleton
    @Provides
    public static Cicerone<Router> provideCicerone() {
        return (Cicerone<Router>) mock(Cicerone.class);
    }

    @Singleton
    @Provides
    public static Router provideRouter(Cicerone<Router> cicerone) {
        return mock(Router.class);
    }

    @Singleton
    @Provides
    public static NavigatorHolder provideNavigatorHolder(Cicerone<Router> cicerone) {
        return mock(NavigatorHolder.class);
    }

    @Singleton
    @Provides
    public static QrCodeHelper provideQrCodeParser() {
        return mock(QrCodeHelper.class);
    }

    @Named(Constants.UI_SCHEDULER)
    @Singleton
    @Provides
    public static Scheduler provideUIObserveScheduler() {
        return Schedulers.trampoline();
    }
}
