package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

@Config(id = "debug", group = Configuration.MODID)
public final class ArrayDebugConfig {

    @Configurable
    public int[] arr = { 1, 10 };

    @Configurable
    public Nest3Child child = new Nest3Child();

    @Configurable
    @Configurable.Synchronized
    public Nest2 nest2 = new Nest2();

    public static class Nest0 {

        @Configurable
        @Configurable.Synchronized
        public boolean nestedBool0 = false;
    }

    public static final class Nest1 {

        @Configurable
        public Nest0 nest0 = new Nest0();
    }

    public static final class Nest2 {

        @Configurable
        public Nest1 nest1 = new Nest1();

        @Configurable
        public Nest3Child child = new Nest3Child();
    }

    public static final class Nest3Child extends Nest0 {

        @Configurable
        @Configurable.Range(min = 1, max = 128)
        public long myLong3 = 123L;
    }
}
