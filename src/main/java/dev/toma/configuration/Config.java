package dev.toma.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * @return Your mod ID
     */
    String value();

    /**
     * @return Name of your config file. <b>This is required when using multiple config files</b>
     */
    String filename() default "";

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Entry {

    }
}
