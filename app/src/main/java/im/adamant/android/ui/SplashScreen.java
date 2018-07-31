package im.adamant.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.franmontiel.localechanger.LocaleChanger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.AuthorizeInteractor;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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

        if (authorizeInteractor.isAuthorized()){
            goToScreen(MainScreen.class, applicationContext, thisReference);
        } else {
            Disposable subscribe = authorizeInteractor
                    .restoreAuthorization()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(authorization -> {
                        SplashScreen splashScreen = thisReference.get();
                        Context activityContext = applicationContext;
                        if (splashScreen != null){
                            activityContext = splashScreen;
                        }

                        if (authorization.isSuccess()){
                            goToScreen(MainScreen.class, applicationContext, thisReference);
                        } else {
                            Toast.makeText(activityContext, R.string.account_not_found, Toast.LENGTH_LONG).show();
                            goToScreen(LoginScreen.class, applicationContext, thisReference);
                        }
                    })
                    .doOnError(error -> {
                        if (error instanceof IOException){
                            SplashScreen splashScreen = thisReference.get();
                            Context activityContext = applicationContext;
                            if (splashScreen != null){
                                activityContext = splashScreen;
                            }

                            Toast.makeText(activityContext, R.string.authorization_error, Toast.LENGTH_LONG).show();
                        } else {
                            goToScreen(LoginScreen.class, applicationContext, thisReference);
                        }

                    })
                    .retry((integer, throwable) -> throwable instanceof IOException)
                    .onErrorReturn((throwable) -> {
                        Authorization authorization = new Authorization();
                        authorization.setSuccess(false);

                        return authorization;
                    })
                    .subscribe();

            subscriptions.add(subscribe);
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
