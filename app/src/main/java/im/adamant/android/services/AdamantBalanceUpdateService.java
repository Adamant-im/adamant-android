package im.adamant.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.BuildConfig;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.dagger.AdamantBalanceUpdateServiceModule;
import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class AdamantBalanceUpdateService extends Service {
    private static final String TAG = "ADMBalanceService";
    private final IBinder binder = new AdamantBalanceUpdateService.LocalBinder();
    private static final PublishSubject<Object> updateSubject = PublishSubject.create();

    enum Irrelevant { INSTANCE }

    @Named(AdamantBalanceUpdateServiceModule.NAME)
    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    AdamantApiWrapper api;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();

        startListenBalance(updateSubject);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (compositeDisposable != null){
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
    }

    public void updateBalanceImmediately() {
        updateSubject.onNext(new Object());
    }

    private void startListenBalance(final PublishSubject<Object> subject){
        Disposable subscribe =
                api
                    .updateBalance()
                    .repeatWhen((completed) -> completed.delay(
                            BuildConfig.UPDATE_BALANCE_SECONDS_DELAY,
                            TimeUnit.SECONDS
                    ))
                    .retryWhen((completed) -> completed.delay(
                            BuildConfig.UPDATE_BALANCE_SECONDS_DELAY,
                            TimeUnit.SECONDS
                    ))
                    .repeatWhen(repeatHandler ->
                            repeatHandler.flatMap(nothing -> subject.toFlowable(BackpressureStrategy.LATEST)))
                    .subscribe();

        compositeDisposable.add(subscribe);
    }

    public class LocalBinder extends Binder {
        public AdamantBalanceUpdateService getService() {
            return AdamantBalanceUpdateService.this;
        }
    }
}
