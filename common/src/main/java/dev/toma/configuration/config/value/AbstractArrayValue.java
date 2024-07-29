package dev.toma.configuration.config.value;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.message.FormattedMessage;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class AbstractArrayValue<T> extends ConfigValue<T[]> implements IArrayValue<T> {

    private boolean fixedSize;

    public AbstractArrayValue(ValueData<T[]> valueData) {
        super(valueData);
    }

    @Override
    public boolean isFixedSize() {
        return fixedSize;
    }

    @Override
    protected T[] validateValue(T[] in) {
        T[] defaultArray = this.valueData.getDefaultValue();
        int defaultSize = defaultArray.length;
        int valueSize = in.length;
        if (this.fixedSize && valueSize != defaultSize) {
            ConfigUtils.logArraySizeCorrectedMessage(this.getId(), Arrays.toString(in), Arrays.toString(defaultArray));
            return defaultArray;
        }
        return in;
    }

    @Override
    protected void readFieldData(Field field) {
        this.fixedSize = field.isAnnotationPresent(Configurable.FixedSize.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<IConfigValue<V>> getChild(Iterator<String> iterator, Class<V> targetType) {
        try {
            return Optional.of((IConfigValue<V>) this); // This will break for object arrays, but since that is currently not supported we can just ignore it. Maybe it will hurt us later
        } catch (ClassCastException e) {
            if (Configuration.PLATFORM.isDevelopmentEnvironment()) {
                Configuration.LOGGER.error(new FormattedMessage("Attempted to load invalid config value class for array {}", this.getId()), e);
            }
            return Optional.empty();
        }
    }

    @Override
    public <V> Optional<V> getChildValue(Iterator<String> iterator, Class<V> targetType) {
        Optional<IConfigValue<V>> optional = this.getChild(iterator, targetType);
        if (optional.isEmpty())
            return Optional.empty();
        String key = iterator.next();
        T[] arrayValue = this.get(Mode.SAVED);
        try {
            int length = Array.getLength(arrayValue);
            int elementIndex = Integer.parseInt(key);
            if (elementIndex < 0 || elementIndex >= length) {
                Configuration.LOGGER.warn("Attempted to get array config value {} which is out of bounds!", key);
                return Optional.empty();
            }
            Object item = Array.get(arrayValue, elementIndex);
            if (iterator.hasNext()) {
                if (item instanceof IHierarchical hierarchical) {
                    return hierarchical.getChildValue(iterator, targetType);
                }
                Configuration.LOGGER.warn("Attempted to get non-existing value {} in config!", key);
            } else {
                if (targetType.isAssignableFrom(item.getClass())) {
                    return Optional.of(targetType.cast(item));
                }
                Configuration.LOGGER.warn("Attempted to get invalid value type {} in config!", key);
            }
            return Optional.empty();
        } catch (Exception e) {
            Configuration.LOGGER.error(new FormattedMessage("Failed to obtain child value for key {} due to error", key), e);
            return Optional.empty();
        }
    }

    @Override
    public IConfigValue<?> getChildById(String childId) {
        return null;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.get());
    }

    @Override
    protected boolean isChanged(T[] saved, T[] pending) {
        return this.isEditable() && !Arrays.equals(saved, pending);
    }

    public static <T> void saveToBuffer(T[] value, FriendlyByteBuf buf, BiConsumer<FriendlyByteBuf, T> encoder) {
        buf.writeInt(value.length);
        for (T t : value) {
            encoder.accept(buf, t);
        }
    }

    public static <T> T[] readFromBuffer(FriendlyByteBuf buf, IntFunction<T[]> factory, Function<FriendlyByteBuf, T> decoder) {
        int length = buf.readInt();
        T[] value = factory.apply(length);
        for (int i = 0; i < length; i++) {
            value[i] = decoder.apply(buf);
        }
        return value;
    }
}
