package im.adamant.android.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.helpers.Settings;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

@Module
public abstract class GeneralModule {
    @Singleton
    @Provides
    public static Gson provideGson() {
        return new Gson();
    }

    @Singleton
    @Provides
    public static GsonBuilder provideGsonBuilder() {
        return new GsonBuilder();
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
    public static Settings provideSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return new Settings(preferences);
    }

    @Singleton
    @Provides
    public static Cicerone<Router> provideCicerone() {
        return Cicerone.create();
    }

    @Singleton
    @Provides
    public static Router provideRouter(Cicerone<Router> cicerone) {
        return cicerone.getRouter();
    }

    @Singleton
    @Provides
    public static NavigatorHolder provideNavigatorHolder(Cicerone<Router> cicerone) {
        return cicerone.getNavigatorHolder();
    }

    @Singleton
    @Provides
    public static QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }
}
