package dev.toma.configuration.api.type;

import dev.toma.configuration.api.ConfigSortIndexes;

public class EnumType<T extends Enum<T>> extends ArrayType<T> {

    public EnumType(String name, T value, String... desc) {
        super(name, value, value.getDeclaringClass().getEnumConstants(), desc);
    }

    @Override
    protected String getDefaultElementString(T t) {
        return t.name();
    }

    @Override
    public int getSortIndex() {
        return ConfigSortIndexes.ENUM;
    }
}
