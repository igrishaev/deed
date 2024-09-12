package pinny;

import java.util.Map;

public record MEntry(
        Object key,
        Object val
) implements Map.Entry<Object,Object> {
    @Override
    public Object getKey() {
        return key;
    }
    @Override
    public Object getValue() {
        return val;
    }
    @Override
    public Object setValue(final Object value) {
        throw Err.error("cannot set value for entry, value: %s", value);
    }
    @Override
    public String toString() {
        return String.format("MEntry<%s, %s>", key, val);
    }
}
