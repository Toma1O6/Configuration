package dev.toma.configuration.internal;

import dev.toma.configuration.api.*;
import dev.toma.configuration.api.type.*;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigWriter implements IConfigWriter {

    private ObjectType writingObject;

    @Override
    public <T extends IConfigType<?>> T write(T configType) {
        writingObject.get().put(configType.getId(), configType);
        return configType;
    }

    @Override
    public <T extends ObjectType> T writeObject(Function<IObjectSpec, T> creator, String name, String... description) {
        IConfigWriter writer = new ConfigWriter();
        IObjectSpec spec = new ObjectSpec(name, writer, description);
        T type = creator.apply(spec);
        return write(type);
    }

    @Override
    public <T extends IConfigType<?>> CollectionType<T> writeList(String name, Supplier<T> elementFactory, String... description) {
        CollectionType<T> type = new CollectionType<>(name, elementFactory, description);
        return write(type);
    }

    @Override
    public <T extends IConfigType<?>> CollectionType<T> writeFillList(String name, List<T> list, Supplier<T> elementFactory, String... description) {
        CollectionType<T> type = new CollectionType<>(name, list, elementFactory, description);
        return write(type);
    }

    @Override
    public <T extends IConfigType<?>> CollectionType<T> writeApplyList(String name, Supplier<T> elementFactory, Consumer<List<T>> action, String... description) {
        CollectionType<T> type = writeList(name, elementFactory, description);
        action.accept(type.get());
        return type;
    }

    @Override
    public <T extends IConfigType<?>> ArrayType<T> writeArray(String name, T selectedValue, T[] values, String... description) {
        ArrayType<T> type = new ArrayType<>(name, selectedValue, values, description);
        return write(type);
    }

    @Override
    public <T extends IConfigType<?>> ArrayType<T> writeArray(String name, int selectedIndex, T[] values, String... description) {
        ArrayType<T> type = new ArrayType<>(name, values[MathHelper.clamp(selectedIndex, 0, values.length - 1)], values, description);
        return write(type);
    }

    @Override
    public <T extends Enum<T>> EnumType<T> writeEnum(String name, T selectedValue, String... description) {
        EnumType<T> type = new EnumType<>(name, selectedValue, description);
        return write(type);
    }

    @Override
    public ColorType writeColor(String name, String color, IRestriction<String> restriction, String... description) {
        ColorType type = new ColorType(name, color, restriction, description);
        return write(type);
    }

    @Override
    public ColorType writeColorRGB(String name, String color, String... desc) {
        return writeColor(name, color, Restrictions.colorRestriction(3), desc).setSolidRender();
    }

    @Override
    public ColorType writeColorARGB(String name, String color, String... desc) {
        return writeColor(name, color, Restrictions.colorRestriction(4), desc);
    }

    @Override
    public StringType writeString(String name, String value, String... desc) {
        StringType type = new StringType(name, value, desc);
        return write(type);
    }

    @Override
    public StringType writeRestrictedString(String name, String value, IRestriction<String> restriction, String... desc) {
        StringType type = new StringType(name, value, restriction, desc);
        return write(type);
    }

    @Override
    public DoubleType writeDouble(String name, double value, String... desc) {
        DoubleType type = new DoubleType(name, value, desc);
        return write(type);
    }

    @Override
    public DoubleType writeBoundedDouble(String name, double value, double min, double max, String... desc) {
        DoubleType type = new DoubleType(name, value, min, max, desc);
        return write(type);
    }

    @Override
    public IntType writeInt(String name, int value, String... desc) {
        IntType type = new IntType(name, value, desc);
        return write(type);
    }

    @Override
    public IntType writeBoundedInt(String name, int value, int min, int max, String... desc) {
        IntType type = new IntType(name, value, min, max, desc);
        return write(type);
    }

    @Override
    public BooleanType writeBoolean(String name, boolean value, String... desc) {
        BooleanType type = new BooleanType(name, value, desc);
        return write(type);
    }

    @Override
    public void setWritingObject(ObjectType writingObject) {
        this.writingObject = writingObject;
    }
}
