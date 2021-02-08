package dev.toma.configuration.api.type;

import dev.toma.configuration.api.util.Nameable;

public class EnumType<T extends Enum<T> & Nameable> extends FixedCollectionType<T> {

    public EnumType(String name, T value, String... desc) {
        super(name, value, value.getDeclaringClass().getEnumConstants(), desc);
    }

    @Override
    public int getSortIndex() {
        return 4;
    }
}
