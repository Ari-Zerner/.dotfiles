package rpn;

import java.util.Iterator;

/**
 * An immutable stack similar to a Clojure list.
 */
public class PersistentStack<T> implements Iterable<T> {

    private final T head;
    private final PersistentStack<T> tail;

    private PersistentStack(T head, PersistentStack<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public PersistentStack(T bottom) {
        head = bottom;
        tail = this;
    }

    public PersistentStack<T> push(T value) {
        return new PersistentStack<>(value, this);
    }

    public PersistentStack<T> pop() {
        return tail;
    }

    public T peek() {
        return head;
    }

    public boolean empty() {
        return tail == this;
    }

    @Override
    public Iterator<T> iterator() {
        final PersistentStack<T> start = this;
        return new Iterator<T>() {
            private PersistentStack<T> stack = start;

            @Override
            public boolean hasNext() {
                return !stack.empty();
            }

            @Override
            public T next() {
                T next = stack.peek();
                stack = stack.pop();
                return next;
            }
        };
    }
}
