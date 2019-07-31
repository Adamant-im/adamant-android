package im.adamant.android.rx;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ThreadSafeObservableRxList<T> extends AbstractObservableRxList<T> {

    @Override
    protected List<T> provideCollection() {
        return new CopyOnWriteArrayList<>();
    }
}
