package im.adamant.android.rx;

import java.util.ArrayList;
import java.util.List;

public class ThreadUnsafeObservableRxList<T> extends AbstractObservableRxList<T> {

    public ThreadUnsafeObservableRxList(AbstractObservableRxList<T> collection) {
        this.delegateCollection.addAll(collection);
        collection.subject.subscribe(this.subject);
    }

    public ThreadUnsafeObservableRxList() {
    }

    @Override
    protected List<T> provideCollection() {
        return new ArrayList<>();
    }
}
