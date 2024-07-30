package dev.toma.configuration.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Config marker annotation. Every registered config class must have this annotation.
 * Inside this class you should define all configurable fields <b>(cannot be both {@code static} or {@code final})!</b>.
 * All configurable fields must be annotated with {@link Configurable} annotation, otherwise it will
 * be ignored. <br>
 * <b>If your {@code configID} is different from your {@code modID}, you need to define {@link Config#group()} attribute to equal your
 * {@code modID} otherwise your config GUI won't be linked.</b>
 *
 * @author Toma
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * This value should be globally unique. It is suggested to use
     * your mod ID as prefix or standalone based on how many configs you're
     * creating.
     *
     * @return Unique config identifier
     */
    String id();

    /**
     * Allows you to customize your config filename. Your custom filename must be valid
     * according to your operating system, otherwise {@link java.io.IOException} will
     * be raised during config processing.
     * Using {@code empty} string as filename will use your {@link Config#id()} value as default.
     *
     * @return Your custom filename.
     */
    String filename() default "";

    /**
     * Allows you to group multiple configs under one identifier.
     * Useful when you have 2 or more config files which should be accessible via GUI or also config ID which does not match your mod ID.
     *
     * @return Custom config group identifier. By default, value defined by {@link Config#id()} will be used.
     */
    String group() default "";

    /**
     * Annotating your config class with this will block config auto-sync when config file is updated
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NoAutoSync {}
}
