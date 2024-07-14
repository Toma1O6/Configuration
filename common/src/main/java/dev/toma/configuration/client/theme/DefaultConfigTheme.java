package dev.toma.configuration.client.theme;

import dev.toma.configuration.client.theme.adapter.DisplayAdapter;
import dev.toma.configuration.config.adapter.TypeMatcher;
import net.minecraft.resources.ResourceLocation;

public class DefaultConfigTheme extends ConfigTheme {

    public static final ConfigTheme DEFAULT = new DefaultConfigTheme(null);

    public DefaultConfigTheme() {
        this(null);
    }

    public DefaultConfigTheme(ResourceLocation backgroundTexture) {
        setBackgroundTexture(backgroundTexture);
        setHeader(new Header(null, 35, 0x99 << 24, 0xaaaaaa));
        setFooter(new Footer(30, 0x99 << 24));
        setScrollbar(new Scrollbar(false, 5, 0xFF << 24));
        setBackgroundFillColor(0x55 << 24);

        registerDisplayAdapter(TypeMatcher.matchBoolean(), DisplayAdapter.booleanValue());
        registerDisplayAdapter(TypeMatcher.matchCharacter(), DisplayAdapter.characterValue());
        registerDisplayAdapter(TypeMatcher.matchInteger(), DisplayAdapter.integerValue());
        registerDisplayAdapter(TypeMatcher.matchLong(), DisplayAdapter.longValue());
        registerDisplayAdapter(TypeMatcher.matchFloat(), DisplayAdapter.floatValue());
        registerDisplayAdapter(TypeMatcher.matchDouble(), DisplayAdapter.doubleValue());
        registerDisplayAdapter(TypeMatcher.matchString(), DisplayAdapter.stringValue());
        registerDisplayAdapter(TypeMatcher.matchBooleanArray(), DisplayAdapter.booleanArrayValue());
        registerDisplayAdapter(TypeMatcher.matchIntegerArray(), DisplayAdapter.integerArrayValue());
        registerDisplayAdapter(TypeMatcher.matchLongArray(), DisplayAdapter.longArrayValue());
        registerDisplayAdapter(TypeMatcher.matchFloatArray(), DisplayAdapter.floatArrayValue());
        registerDisplayAdapter(TypeMatcher.matchDoubleArray(), DisplayAdapter.doubleArrayValue());
        registerDisplayAdapter(TypeMatcher.matchStringArray(), DisplayAdapter.stringArrayValue());
        registerDisplayAdapter(TypeMatcher.matchEnum(), DisplayAdapter.enumValue());
        registerDisplayAdapter(TypeMatcher.matchEnumArray(), DisplayAdapter.enumArrayValue());
        registerDisplayAdapter(TypeMatcher.matchObject(), DisplayAdapter.objectValue());
    }
}
