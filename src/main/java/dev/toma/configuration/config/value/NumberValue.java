package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.NumberDisplayType;

import java.lang.reflect.Field;

public abstract class NumberValue<N extends Number> extends ConfigValue<N> {

    protected NumberDisplayType displayType = NumberDisplayType.TEXT_FIELD;

    public NumberValue(ValueData<N> valueData) {
        super(valueData);
    }

    @Override
    protected void readFieldData(Field field) {
        Configurable.Gui.NumberDisplay display = field.getAnnotation(Configurable.Gui.NumberDisplay.class);
        if (display != null) {
            this.displayType = display.value();
        }
    }
}
