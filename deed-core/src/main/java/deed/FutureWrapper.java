package deed;

import clojure.lang.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public record FutureWrapper(
        Object x,
        IPersistentMap meta
) implements IDeref, IBlockingDeref, IPending, IObj, IMeta, Future<Object> {

    public static FutureWrapper of(final Object x) {
        return new FutureWrapper(x, null);
    }

    @Override
    public Object deref(long ms, Object timeoutValue) {
        return x;
    }

    @Override
    public Object deref() {
        return x;
    }

    @Override
    public boolean isRealized() {
        return true;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public Object get() {
        return x;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) {
        return x;
    }

    @Override
    public IObj withMeta(IPersistentMap meta) {
        return new FutureWrapper(x, meta);
    }
}
