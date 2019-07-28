package im.adamant.android.rx;

import java.util.ArrayList;
import java.util.List;

public class ThreadUnsafeObservableRxList<T> extends AbstractObservableRxList<T> {
    @Override
    protected List<T> provideCollection() {
        return new ArrayList<>();
    }
}
