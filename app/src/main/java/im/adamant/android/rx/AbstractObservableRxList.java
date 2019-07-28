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
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

//TODO: Unstable collection implementation. See retainAll for example. (Not all items in the collection can be delete)
public abstract class AbstractObservableRxList<T> implements List<T> {
    protected final List<T> list;
    protected final PublishSubject<RxListEvent<T>> subject;

    public AbstractObservableRxList() {
        list = provideCollection();
        this.subject = PublishSubject.create();
    }

    public AbstractObservableRxList(List<T> list) {
        this();
        addAll(list);
    }

    protected abstract List<T> provideCollection();

    public void wasUpdated(T value) {
        int position = list.indexOf(value);
        subject.onNext(new RxListEvent<T>(ChangeType.UPDATE, position, 1));
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        int size = list.size();
        list.clear();
        subject.onNext(new RxListEvent<T>(ChangeType.CLEAR, 0, size));
    }

    public Observable<RxListEvent<T>> getEventObservable() {
        return subject;
    }

    public Observable<T> getItemObservable() {
        return Observable.fromIterable(list);
    }


    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return list.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(@Nullable T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean add = list.add(t);
        subject.onNext(new RxListEvent<T>(ChangeType.ADD, list.size() - 2, 1));
        return add;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        int position = list.indexOf(o);
        boolean removed = list.remove(o);
        if (removed) {
            subject.onNext(new RxListEvent<T>(ChangeType.REMOVE, position, 1));
        }
        return removed;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        boolean b = list.addAll(c);
        if (b) {
            subject.onNext(new RxListEvent<T>(ChangeType.ADD_COLLECTION, list.size() - 1, c.size()));
        }
        return b;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        boolean b = list.addAll(index, c);
        if (b) {
            subject.onNext(new RxListEvent<T>(ChangeType.ADD_COLLECTION, index, c.size()));
        }
        return b;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        boolean removed = list.removeAll(c);
        if (removed) {
            subject.onNext(new RxListEvent<T>(ChangeType.REMOVE_COLLECTION, 0, c.size()));
        }
        return removed;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        boolean removed = list.retainAll(c);
        if (removed) {
            subject.onNext(new RxListEvent<T>(ChangeType.REMOVE_COLLECTION, 0, c.size()));
        }
        return removed;
    }

    @Override
    public T set(int index, T element) {
        T item = list.set(index, element);
        subject.onNext(new RxListEvent<T>(ChangeType.SET, index, 1));
        return item;
    }

    @Override
    public void add(int index, T element) {
        list.add(element);
        subject.onNext(new RxListEvent<T>(ChangeType.ADD, index, 1));
    }

    @Override
    public T remove(int index) {
        T removed = list.remove(index);
        subject.onNext(new RxListEvent<T>(ChangeType.REMOVE, index, 1));
        return removed;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return list.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public void replaceAll(@NonNull UnaryOperator<T> operator) {
        list.replaceAll(operator);
        subject.onNext(new RxListEvent<T>(ChangeType.REPLACE_ALL, 0, 0));
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public void sort(@Nullable Comparator<? super T> c) {
        list.sort(c);
        subject.onNext(new RxListEvent<T>(ChangeType.SORT, 0, 0));
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        boolean removeIf = list.removeIf(filter);
        subject.onNext(new RxListEvent<T>(ChangeType.REMOVE, 0, 0));
        return removeIf;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public Stream<T> stream() {
        return list.stream();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public Stream<T> parallelStream() {
        return list.parallelStream();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
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
