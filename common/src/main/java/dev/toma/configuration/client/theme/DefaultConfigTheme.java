package dev.toma.configuration.client.theme;

import dev.toma.configuration.client.screen.AbstractConfigScreen;
import dev.toma.configuration.client.theme.adapter.*;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.client.widget.render.SolidColorBackgroundRenderer;
import dev.toma.configuration.client.widget.render.SpriteBackgroundRenderer;
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

        registerDisplayAdapter(TypeMatcher.matchBoolean(), new BooleanDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchCharacter(), new CharacterDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchByte(), new ByteDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchShort(), new ShortDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchInteger(), new IntegerDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchLong(), new LongDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchFloat(), new FloatDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchDouble(), new DoubleDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchString(), new StringDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchBooleanArray(), DisplayAdapter.booleanArrayValue());
        registerDisplayAdapter(TypeMatcher.matchIntegerArray(), DisplayAdapter.integerArrayValue());
        registerDisplayAdapter(TypeMatcher.matchLongArray(), DisplayAdapter.longArrayValue());
        registerDisplayAdapter(TypeMatcher.matchFloatArray(), DisplayAdapter.floatArrayValue());
        registerDisplayAdapter(TypeMatcher.matchDoubleArray(), DisplayAdapter.doubleArrayValue());
        registerDisplayAdapter(TypeMatcher.matchStringArray(), DisplayAdapter.stringArrayValue());
        registerDisplayAdapter(TypeMatcher.matchEnum(), DisplayAdapter.enumValue());
        registerDisplayAdapter(TypeMatcher.matchEnumArray(), DisplayAdapter.enumArrayValue());
        registerDisplayAdapter(TypeMatcher.matchObject(), DisplayAdapter.objectValue());

        setButtonBackground(t -> new SpriteBackgroundRenderer(() -> AbstractConfigScreen.BUTTON_SPRITES.get(t.isActive(), t.isHoveredOrFocused())));
        setEditBoxBackground(t -> new SpriteBackgroundRenderer(() -> EditBoxWidget.SPRITES.get(t.isActive(), t.isHoveredOrFocused())));
        setSliderBackground(t -> new SpriteBackgroundRenderer(() -> SliderWidget.SLIDER.get(t.isActive(), t.isHoveredOrFocused())));
        setSliderHandle(t -> new SpriteBackgroundRenderer(() -> SliderWidget.HANDLE.get(t.isActive(), t.isFocused())));
        setColorBackground(t -> new SolidColorBackgroundRenderer(() -> t.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFA0A0A0));
    }
}
