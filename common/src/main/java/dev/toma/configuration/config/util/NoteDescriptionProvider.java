package dev.toma.configuration.config.util;

import dev.toma.configuration.config.UpdateRestrictions;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class NoteDescriptionProvider<T> implements IDescriptionProvider<T> {

    public static final MutableComponent SYNCHRONIZED = Component.translatable("text.configuration.description.synchronized");
    public static final Function<UpdateRestrictions, MutableComponent> RESTRICTION = t -> Component.translatable("text.configuration.description.update_on", t.getLabel());

    @Override
    public List<Component> generate(IConfigValueReadable<T> value) {
        List<Component> components = new ArrayList<>();
        this.appendValues(value, comp -> components.add(comp.withStyle(ChatFormatting.DARK_GRAY)));
        return components;
    }

    public abstract void appendValues(IConfigValueReadable<T> value, Consumer<MutableComponent> appender);

    public static <T> IDescriptionProvider<T> note(MutableComponent note) {
        return new SimpleNoteImpl<>(note);
    }

    @Override
    public boolean replaceDefaultDescription() {
        return false;
    }

    private static class SimpleNoteImpl<T> extends NoteDescriptionProvider<T> {

        private final MutableComponent label;

        SimpleNoteImpl(MutableComponent label) {
            this.label = label;
        }

        @Override
        public void appendValues(IConfigValueReadable<T> value, Consumer<MutableComponent> appender) {
            appender.accept(label);
        }
    }
}
