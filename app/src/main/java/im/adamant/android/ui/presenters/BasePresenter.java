package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import im.adamant.android.rx.RxTaskManager;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends MvpView> extends MvpPresenter<V> {
    protected CompositeDisposable subscriptions = new CompositeDisposable();

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.dispose();
    }
}
