package dev.toma.configuration.io;

import java.lang.reflect.Field;

public interface ITypeMatcher<T> {

    ITypeAdapter<T> findAdapter(Field field);

    default int matchingPriority() {
        return 0;
    }

    static <T> ITypeMatcher<T> matchClass(Class<T> aClass, ITypeAdapter<T> adapter) {
        return matchClass(aClass, adapter, 0);
    }

    static <T> ITypeMatcher<T> matchClass(Class<T> aClass, ITypeAdapter<T> adapter, int priority) {
        return new ITypeMatcher<T>() {
            @Override
            public ITypeAdapter<T> findAdapter(Field field) {
                return field.getType().equals(aClass) ? adapter : null;
            }

            @Override
            public int matchingPriority() {
                return priority;
            }
        };
    }

    static <T> ITypeMatcher<T> primitive(Class<T> t1, Class<T> t2, ITypeAdapter<T> adapter) {
        return primitive(t1, t2, adapter, 0);
    }

    static <T> ITypeMatcher<T> primitive(Class<T> t1, Class<T> t2, ITypeAdapter<T> adapter, int priority) {
        return new ITypeMatcher<T>() {
            @Override
            public ITypeAdapter<T> findAdapter(Field field) {
                Class<?> fieldType = field.getType();
                return fieldType.equals(t1) || fieldType.equals(t2) ? adapter : null;
            }

            @Override
            public int matchingPriority() {
                return priority;
            }
        };
    }
}
