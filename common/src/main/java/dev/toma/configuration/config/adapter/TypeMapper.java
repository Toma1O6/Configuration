package dev.toma.configuration.config.adapter;

import java.util.function.Function;

public interface TypeMapper<T, R> {

    R migrate(T t);

    T rollback(R r);

    static <T> TypeMapper<T, T> identity() {
        return of(t -> t, t -> t);
    }

    static <T, R> TypeMapper<T, R> of(Function<T, R> encoder, Function<R, T> decoder) {
        return new TypeMapper<>() {
            @Override
            public R migrate(T t) {
                return encoder.apply(t);
            }

            @Override
            public T rollback(R r) {
                return decoder.apply(r);
            }
        };
    }
}
