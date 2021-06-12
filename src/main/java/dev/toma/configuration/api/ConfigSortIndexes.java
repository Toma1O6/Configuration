package dev.toma.configuration.api;

/**
 * Collection of common sorting indexes used by default types.
 * There is significant gap between all values since you may
 * want to put some custom types in between default types.
 */
public final class ConfigSortIndexes {

    public static final int BOOLEAN = 0;
    //public static final int BYTE = 10;
    //public static final int SHORT = 20;
    public static final int INT = 30;
    //public static final int LONG = 40;
    //public static final int FLOAT = 50;
    public static final int DOUBLE = 60;
    public static final int STRING = 70;
    public static final int ENUM = 80;
    public static final int ARRAY = 90;
    public static final int COLLECTION = 100;
    public static final int OBJECT = 110;
    public static final int LAST = Integer.MAX_VALUE;
}
