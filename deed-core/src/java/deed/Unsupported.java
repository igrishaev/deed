package deed;

import clojure.lang.IDeref;
import clojure.lang.Keyword;
import clojure.lang.PersistentHashMap;

public record Unsupported(
        String className,
        String content
) implements IDeref {

    public static Unsupported of(final Object x) {
        final String className = x.getClass().getName();
        final String content = x.toString();
        return new Unsupported(className, content);
    }

    private final static Keyword kwClass = Keyword.intern("class");
    private final static Keyword kwContent = Keyword.intern("content");

    @Override
    public Object deref() {
        return PersistentHashMap.create(
                kwClass, className,
                kwContent, content
        );
    }
}
