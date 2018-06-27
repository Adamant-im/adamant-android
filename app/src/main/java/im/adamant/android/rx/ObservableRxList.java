package im.adamant.android.rx;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ObservableRxList<T> {
    protected final List<T> list = new CopyOnWriteArrayList<>();
    protected final PublishSubject<RxList<T>> subject;

    public ObservableRxList() {
        this.subject = PublishSubject.create();
    }

    public ObservableRxList(List<T> list) {
        this();
        addAll(list);
    }

    public void add(T value) {
        list.add(value);
        subject.onNext(new RxList<T>(ChangeType.ADD, value));
    }

    public void addAll(List<T> items) {
        for (T aList : items) {
            add(aList);
        }
    }

    public void addAll(Set<T> items) {
        for (T aList : items) {
            add(aList);
        }
    }

    public boolean contains(T item) {
        return list.contains(item);
    }

    public void wasUpdated(T value) {
            subject.onNext(new RxList<T>(ChangeType.UPDATE, value));
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
        subject.onNext(new RxList<T>(ChangeType.CLEAR, null));
    }

    public void remove(T value) {
        list.remove(value);
        subject.onNext(new RxList<T>(ChangeType.REMOVE, value));
    }

    public Observable<RxList<T>> getObservable() {
        return subject;
    }

    public Observable<T> getCurrentList() {
        return Observable.fromIterable(list);
    }

    public enum ChangeType {
        ADD, REMOVE, UPDATE, CLEAR
    }

    public static class RxList<T> {
        public ChangeType changeType;
        public T item;

        public RxList(ChangeType changeType, T item) {
            this.changeType = changeType;
            this.item = item;
        }
    }
}
