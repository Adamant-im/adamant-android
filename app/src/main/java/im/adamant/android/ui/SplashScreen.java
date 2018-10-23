package im.adamant.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.franmontiel.localechanger.LocaleChanger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.presenters.PincodePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static im.adamant.android.ui.PinCodeScreen.ARG_MODE;

public class SplashScreen extends AppCompatActivity {

    @Inject
    AuthorizeInteractor authorizeInteractor;

    @Inject
    Settings settings;

    @Named(Screens.SPLASH_SCREEN)
    @Inject
    CompositeDisposable subscriptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Context applicationContext = getApplicationContext();
        WeakReference<SplashScreen> thisReference = new WeakReference<>(this);

        if (!settings.isKeyPairMustBeStored()){
            goToScreen(LoginScreen.class, applicationContext, thisReference);
            return;
        }

        setContentView(R.layout.activity_splash_screen);
        //TODO: Проверь что при перевороте экрана авторизация возобновляется
        if (authorizeInteractor.isAuthorized()){
            goToScreen(MainScreen.class, applicationContext, thisReference);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ARG_MODE, PincodePresenter.Mode.RESTORE_KEYPAIR);

            Intent intent = new Intent(applicationContext, PinCodeScreen.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);

            this.startActivityForResult(intent, Constants.PINCODE_WAS_ENTERED);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (subscriptions != null) {
            subscriptions.dispose();
        }
    }

    private static void goToScreen(Class target, Context context, WeakReference<SplashScreen> splashScreenWeakReference) {
        Intent intent = new Intent(context, target);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
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
