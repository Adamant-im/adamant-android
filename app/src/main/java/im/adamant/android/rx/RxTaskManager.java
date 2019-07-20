package im.adamant.android.rx;


import android.util.LongSparseArray;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RxTaskManager {
    private static final int DEFAULT_TASK_TIMEOUT_SECONDS = 60 * 5;
    private LongSparseArray<Disposable> disposableAddress = new LongSparseArray<>();

    public <T> void doIt(
            Flowable<T> flowable,
            Consumer<? super T> success,
            Consumer<? super Throwable> error,
            Action complete
    ) {
        long current = disposableAddress.size();

        Disposable subscription = flowable.
                timeout(DEFAULT_TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .doOnError(throwable -> {
                    remove(current);
                })
                .doOnComplete(() -> {
                    remove(current);
                })
                .subscribe(success, error, complete);

        disposableAddress.append(current, subscription);
    }

    public <T> void doIt(
            Single<T> flowable,
            Consumer<? super T> success,
            Consumer<? super Throwable> error
    ) {
        long current = disposableAddress.size();

        Disposable subscription = flowable.
                timeout(DEFAULT_TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .doOnError(throwable -> {
                    remove(current);
                })
                .doAfterSuccess((obj) -> {
                    remove(current);
                })
                .subscribe(success, error);

        disposableAddress.append(current, subscription);
    }

    public void doIt(
            Completable flowable,
            Action complete,
            Consumer<? super Throwable> error
    ) {
        long current = disposableAddress.size();

        Disposable subscription = flowable.
                timeout(DEFAULT_TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .doOnError(throwable -> {
                    remove(current);
                })
                .doOnComplete(() -> {
                    remove(current);
                })
                .subscribe(complete, error);

        disposableAddress.append(current, subscription);
    }

    private void remove(long key) {
        Disposable disposable = disposableAddress.get(key);
        disposable.dispose();
        disposableAddress.remove(key);
    }
}
