package im.adamant.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.franmontiel.localechanger.LocaleChanger;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.ui.mvp_view.PinCodeView;
import io.reactivex.disposables.CompositeDisposable;

public class SplashScreen extends AppCompatActivity {

    @Inject
    Settings settings;

    @Named(Screens.SPLASH_SCREEN)
    @Inject
    CompositeDisposable subscriptions;

    @Inject
    KeyStoreCipher cipher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Context applicationContext = getApplicationContext();
        WeakReference<SplashScreen> thisReference = new WeakReference<>(this);

        if (settings.isKeyPairMustBeStored()){
            Bundle bundle = new Bundle();
            bundle.putSerializable(PinCodeView.ARG_MODE, PinCodeView.MODE.VERIFY);
            goToScreen(PincodeScreen.class, applicationContext, thisReference, bundle);
        } else {
            goToScreen(LoginScreen.class, applicationContext, thisReference, null);
            return;
        }

        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private static void goToScreen(
            Class target,
            Context context,
            WeakReference<SplashScreen> splashScreenWeakReference,
            Bundle bundle
    ) {
        Intent intent = new Intent(context, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);

        SplashScreen splashScreen = splashScreenWeakReference.get();

        if (splashScreen != null){
            splashScreen.finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }
}
