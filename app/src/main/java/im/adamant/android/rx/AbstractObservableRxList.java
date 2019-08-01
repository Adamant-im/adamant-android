package im.adamant.android.rx;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

//TODO: Unstable collection implementation. See retainAll for example. (Not all items in the collection can be delete)
public abstract class AbstractObservableRxList<T> implements List<T> {
    protected final List<T> delegateCollection;
    protected final PublishSubject<RxListEvent<T>> subject;

    public AbstractObservableRxList() {
        delegateCollection = provideCollection();
        this.subject = PublishSubject.create();
    }

    public AbstractObservableRxList(List<T> delegateCollection) {
        this();
        addAll(delegateCollection);
    }

    protected abstract List<T> provideCollection();

    public void wasUpdated(T value) {
        int position = delegateCollection.indexOf(value);
        subject.onNext(new RxListEvent<T>(ChangeType.UPDATE, position, 1));
    }

    public T get(int index) {
        return delegateCollection.get(index);
    }

    public int size() {
        return delegateCollection.size();
    }

    public void clear() {
        int size = delegateCollection.size();
        delegateCollection.clear();
        subject.onNext(new RxListEvent<T>(ChangeType.CLEAR, 0, size));
    }

    public Observable<RxListEvent<T>> getEventObservable() {
        return subject;
    }

    public Observable<T> getItemObservable() {
        return Observable.fromIterable(delegateCollection);
    }


    @Override
    public boolean isEmpty() {
        return delegateCollection.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return delegateCollection.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return delegateCollection.iterator();
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return delegateCollection.toArray();
    }

    @Override
    public <T1> T1[] toArray(@Nullable T1[] a) {
        return delegateCollection.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean add = delegateCollection.add(t);
        subject.onNext(new RxListEvent<T>(ChangeType.ADD, delegateCollection.size() - 2, 1));
        return add;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        int position = delegateCollection.indexOf(o);
        boolean removed = delegateCollection.remove(o);
        if (removed) {
            subject.onNext(new RxListEvent<T>(ChangeType.REMOVE, position, 1));
        }
        return removed;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return delegateCollection.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        boolean b = delegateCollection.addAll(c);
        if (b) {
            subject.onNext(new RxListEvent<T>(ChangeType.ADD_COLLECTION, delegateCollection.size() - 1, c.size()));
        }
        return b;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        boolean b = delegateCollection.addAll(index, c);
        if (b) {
            subject.onNext(new RxListEvent<T>(ChangeType.ADD_COLLECTION, index, c.size()));
        }
        return b;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        boolean removed = delegateCollection.removeAll(c);
        if (removed) {
            subject.onNext(new RxListEvent<T>(ChangeType.REMOVE_COLLECTION, 0, c.size()));
        }
        return removed;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        boolean removed = delegateCollection.retainAll(c);
        if (removed) {
            subject.onNext(new RxListEvent<T>(ChangeType.REMOVE_COLLECTION, 0, c.size()));
        }
        return removed;
    }

    @Override
    public T set(int index, T element) {
        T item = delegateCollection.set(index, element);
        subject.onNext(new RxListEvent<T>(ChangeType.SET, index, 1));
        return item;
    }

    @Override
    public void add(int index, T element) {
        delegateCollection.add(element);
        subject.onNext(new RxListEvent<T>(ChangeType.ADD, index, 1));
    }

    @Override
    public T remove(int index) {
        T removed = delegateCollection.remove(index);
        subject.onNext(new RxListEvent<T>(ChangeType.REMOVE, index, 1));
        return removed;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return delegateCollection.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return delegateCollection.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return delegateCollection.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return delegateCollection.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return delegateCollection.subList(fromIndex, toIndex);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public void replaceAll(@NonNull UnaryOperator<T> operator) {
        delegateCollection.replaceAll(operator);
        subject.onNext(new RxListEvent<T>(ChangeType.REPLACE_ALL, 0, 0));
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public void sort(@Nullable Comparator<? super T> c) {
        delegateCollection.sort(c);
        subject.onNext(new RxListEvent<T>(ChangeType.SORT, 0, 0));
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Spliterator<T> spliterator() {
        return delegateCollection.spliterator();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        boolean removeIf = delegateCollection.removeIf(filter);
        subject.onNext(new RxListEvent<T>(ChangeType.REMOVE, 0, 0));
        return removeIf;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public Stream<T> stream() {
        return delegateCollection.stream();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public Stream<T> parallelStream() {
        return delegateCollection.parallelStream();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer<? super T> action) {
        delegateCollection.forEach(action);
    }

    public enum ChangeType {
        ADD, SET, ADD_COLLECTION, REMOVE, REMOVE_COLLECTION, UPDATE, CLEAR, REPLACE_ALL, SORT
    }

    public static class RxListEvent<T> {
        private ChangeType changeType;
        private int position;
        private int count;

        public RxListEvent(ChangeType changeType, int position, int count) {
            this.changeType = changeType;
        }

        public ChangeType getChangeType() {
            return changeType;
        }

        public int getPosition() {
            return position;
        }

        public int getCount() {
            return count;
        }
    }
}
