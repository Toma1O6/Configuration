package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.io.ConfigIO;

import javax.annotation.Nullable;
import java.util.Objects;

public final class ConfigUtils {

    public static void logCorrectedMessage(String field, @Nullable Object prevValue, Object corrected) {
        Configuration.LOGGER.warn(ConfigIO.MARKER, "Correcting config value '{}' from '{}' to '{}'", field, Objects.toString(prevValue), corrected);
    }

    public static boolean[] unboxArray(Boolean[] values) {
        boolean[] primitive = new boolean[values.length];
        int i = 0;
        for (boolean v : values) {
            primitive[i++] = v;
        }
        return primitive;
    }

    public static int[] unboxArray(Integer[] values) {
        int[] primitive = new int[values.length];
        int i = 0;
        for (int v : values) {
            primitive[i++] = v;
        }
        return primitive;
    }

    public static long[] unboxArray(Long[] values) {
        long[] primitive = new long[values.length];
        int i = 0;
        for (long v : values) {
            primitive[i++] = v;
        }
        return primitive;
    }

    public static float[] unboxArray(Float[] values) {
        float[] primitive = new float[values.length];
        int i = 0;
        for (float v : values) {
            primitive[i++] = v;
        }
        return primitive;
    }

    public static double[] unboxArray(Double[] values) {
        double[] primitive = new double[values.length];
        int i = 0;
        for (double v : values) {
            primitive[i++] = v;
        }
        return primitive;
    }

    public static <E extends Enum<E>> E getEnumConstant(String value, Class<E> declaringClass) throws ConfigValueMissingException {
        E[] constants = declaringClass.getEnumConstants();
        for (E e : constants) {
            if (e.name().equals(value)) {
                return e;
            }
        }
        throw new ConfigValueMissingException("Missing enum value: " + value);
    }
}
