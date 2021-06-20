package dev.toma.configuration.api.type;

import dev.toma.configuration.api.TypeKey;

public class EnumType<T extends Enum<T>> extends ArrayType<T> {

    public EnumType(String name, T value, String... desc) {
        super(TypeKey.ENUM, name, value, value.getDeclaringClass().getEnumConstants(), desc);
    }

    @Override
    protected String getDefaultElementString(T t) {
        return t.name();
    }
}
