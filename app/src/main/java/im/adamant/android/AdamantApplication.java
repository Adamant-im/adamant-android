package im.adamant.android;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.multidex.MultiDexApplication;

import com.franmontiel.localechanger.LocaleChanger;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.dagger.DaggerAppComponent;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.LogoutInteractor;
import io.reactivex.plugins.RxJavaPlugins;

public class AdamantApplication extends MultiDexApplication implements HasActivityInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidActivityInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingAndroidServiceInjector;

    @Inject
    List<Locale> supportedLocales;

    @Inject
    InstrumentationTestFacade instrumentationTestFacade;


    @Override
    public void onCreate() {
        super.onCreate();

        RxJavaPlugins.setErrorHandler(e -> LoggerHelper.e("UNCAUGHT RX", e.getMessage(), e));

        DaggerAppComponent
                .builder()
                .context(this)
                .build()
                .inject(this);

        LocaleChanger.initialize(getApplicationContext(), supportedLocales);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//
//        LeakCanary.install(this);
    }


    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidActivityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingAndroidServiceInjector;
    }


    public static void hideKeyboard(Context ctx, View view) {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Context ctx, View view, int flags) {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), flags);
        }
    }

    public static void showKeyboard(Context ctx, View view, int flags) {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.showSoftInput(view, flags);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleChanger.onConfigurationChanged();
    }

    public InstrumentationTestFacade getInstrumentationTestFacade() {
        return instrumentationTestFacade;
    }
}
