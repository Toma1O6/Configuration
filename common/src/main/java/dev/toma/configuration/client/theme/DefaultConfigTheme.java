package dev.toma.configuration.client.theme;

import dev.toma.configuration.client.screen.AbstractConfigScreen;
import dev.toma.configuration.client.theme.adapter.*;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.client.widget.render.SolidColorRenderer;
import dev.toma.configuration.client.widget.render.SpriteRenderer;
import dev.toma.configuration.config.adapter.TypeMatcher;

public class DefaultConfigTheme extends ConfigTheme {

    public static final ConfigTheme DEFAULT = new DefaultConfigTheme();

    public DefaultConfigTheme() {
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
        registerDisplayAdapter(TypeMatcher.matchBooleanArray(), new BooleanArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchCharacterArray(), new ChararacterArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchByteArray(), new ByteArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchShortArray(), new ShortArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchIntegerArray(), new IntegerArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchLongArray(), new LongArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchFloatArray(), new FloatArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchDoubleArray(), new DoubleArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchStringArray(), new StringArrayDisplayAdapter());
        registerDisplayAdapter(TypeMatcher.matchEnum(), new EnumDisplayAdapter<>());
        registerDisplayAdapter(TypeMatcher.matchEnumArray(), new EnumArrayDisplayAdapter<>());
        registerDisplayAdapter(TypeMatcher.matchObject(), new ObjectDisplayAdapter());

        setButtonBackground(t -> new SpriteRenderer(() -> AbstractConfigScreen.BUTTON_SPRITES.get(t.isActive(), t.isHoveredOrFocused())));
        setEditBoxBackground(t -> new SpriteRenderer(() -> EditBoxWidget.SPRITES.get(t.isActive(), t.isHoveredOrFocused())));
        setSliderBackground(t -> new SpriteRenderer(() -> SliderWidget.SLIDER.get(t.isActive(), t.isHoveredOrFocused())));
        setSliderHandle(t -> new SpriteRenderer(() -> SliderWidget.HANDLE.get(t.isActive(), t.isFocused())));
        setColorBackground(t -> new SolidColorRenderer(() -> t.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFA0A0A0));
    }
}
