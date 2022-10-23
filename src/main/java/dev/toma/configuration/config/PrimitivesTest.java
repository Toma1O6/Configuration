package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.Config;

@Config(value = Configuration.MODID, filename = "configuration-primitives")
public final class PrimitivesTest {

    @Config.Entry
    public final boolean myBooleanValue = true;

    @Config.Entry
    public final byte myByteValue = 13;

    @Config.Entry
    public final short myShortValue = 364;

    @Config.Entry
    public final int myIntValue = 34610;

    @Config.Entry
    public final long myLongValue = 4652453123L;

    @Config.Entry
    public final float myFloatValue = 14.223F;

    @Config.Entry
    public final double myDoubleValue = 1532.15615623D;

    @Config.Entry
    public final char myCharacterValue = 'a';

    @Config.Entry
    public final String myStringValue = "Random Text";
}
