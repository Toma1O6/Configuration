package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.ClientErrors;
import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.ArrayConfigScreen;
import dev.toma.configuration.client.screen.ConfigScreen;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ColorWidget;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.client.widget.EnumWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.validate.NumberRange;
import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.value.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@FunctionalInterface
public interface DisplayAdapter {

    void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container);

    static DisplayAdapter characterValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            EditBox widget = new EditBox(Minecraft.getInstance().font, getValueX(x, width), y, getValueWidth(width), 20, CommonComponents.EMPTY);
            CharValue charValue = (CharValue) value;
            char character = charValue.get();
            widget.setValue(String.valueOf(character));
            widget.setFilter(str -> str.length() <= 1);
            widget.setResponder(str -> {
                if (!str.isEmpty()) {
                    container.setOkStatus();
                    char toSet = str.charAt(0);
                    charValue.setWithValidationHandler(toSet, container);
                } else {
                    container.setValidationResult(ValidationResult.error(ClientErrors.CHAR_VALUE_EMPTY));
                }
            });
            ConfigUtils.adjustCharacterLimit(field, widget);
            return widget;
        });
    }

    static DisplayAdapter booleanArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            BooleanArrayValue arrayValue = (BooleanArrayValue) value;
            BiConsumer<Boolean, Integer> setCallback = (val, i) -> {
                Boolean[] arr = arrayValue.get();
                arr[i] = val;
                arrayValue.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<Boolean, BooleanArrayValue> screen = new ArrayConfigScreen<>(holder, arrayValue, usedScreen);
                screen.fetchSize(() -> arrayValue.get().length);
                screen.valueFactory((id, i) -> {
                    Boolean[] arr = arrayValue.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = value.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<Boolean> typeAttributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, Boolean.TYPE, setCallback, i));
                    return new BooleanValue(ValueData.of(typeAttributes));
                });
                screen.addElement(() -> {
                    Boolean[] arr = arrayValue.get();
                    Boolean[] expanded = new Boolean[arr.length + 1];
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = false;
                    arrayValue.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    Boolean[] arr = arrayValue.get();
                    arrayValue.setValue(trimmer.trim(i, arr, new Boolean[arr.length - 1]));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter integerArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            IntArrayValue arrayValue = (IntArrayValue) value;
            BiConsumer<Integer, Integer> setCallback = (val, i) -> {
                Integer[] arr = arrayValue.get();
                arr[i] = val;
                arrayValue.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<Integer, IntArrayValue> screen = new ArrayConfigScreen<>(holder, arrayValue, usedScreen);
                screen.fetchSize(() -> arrayValue.get().length);
                screen.valueFactory((id, i) -> {
                    Integer[] arr = arrayValue.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = arrayValue.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<Integer> attributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, Integer.TYPE, setCallback, i));
                    return new IntValue(ValueData.of(attributes));
                });
                screen.addElement(() -> {
                    Integer[] arr = arrayValue.get();
                    Integer[] expanded = new Integer[arr.length + 1];
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = Math.max(arrayValue.getRange().min(), 0);
                    arrayValue.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    Integer[] arr = arrayValue.get();
                    arrayValue.setValue(trimmer.trim(i, arr, new Integer[arr.length - 1]));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter longArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            LongArrayValue arrayValue = (LongArrayValue) value;
            BiConsumer<Long, Integer> setCallback = (val, i) -> {
                Long[] arr = arrayValue.get();
                arr[i] = val;
                arrayValue.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<Long, LongArrayValue> screen = new ArrayConfigScreen<>(holder, arrayValue, usedScreen);
                screen.fetchSize(() -> arrayValue.get().length);
                screen.valueFactory((id, i) -> {
                    Long[] arr = arrayValue.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = arrayValue.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<Long> attributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, Long.TYPE, setCallback, i));
                    return new LongValue(ValueData.of(attributes));
                });
                screen.addElement(() -> {
                    Long[] arr = arrayValue.get();
                    Long[] expanded = new Long[arr.length + 1];
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = Math.max(arrayValue.getRange().min(), 0);
                    arrayValue.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    Long[] arr = arrayValue.get();
                    arrayValue.setValue(trimmer.trim(i, arr, new Long[arr.length - 1]));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter floatArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            FloatArrayValue arrayValue = (FloatArrayValue) value;
            BiConsumer<Float, Integer> setCallback = (val, i) -> {
                Float[] arr = arrayValue.get();
                arr[i] = val;
                arrayValue.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<Float, FloatArrayValue> screen = new ArrayConfigScreen<>(holder, arrayValue, usedScreen);
                screen.fetchSize(() -> arrayValue.get().length);
                screen.valueFactory((id, i) -> {
                    Float[] arr = arrayValue.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = arrayValue.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<Float> attributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, Float.TYPE, setCallback, i));
                    return new FloatValue(ValueData.of(attributes));
                });
                screen.addElement(() -> {
                    Float[] arr = arrayValue.get();
                    Float[] expanded = new Float[arr.length + 1];
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = Math.max(arrayValue.getRange().min(), 0);
                    arrayValue.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    Float[] arr = arrayValue.get();
                    arrayValue.setValue(trimmer.trim(i, arr, new Float[arr.length - 1]));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter doubleArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            DoubleArrayValue arrayValue = (DoubleArrayValue) value;
            BiConsumer<Double, Integer> setCallback = (val, i) -> {
                Double[] arr = arrayValue.get();
                arr[i] = val;
                arrayValue.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<Double, DoubleArrayValue> screen = new ArrayConfigScreen<>(holder, arrayValue, usedScreen);
                screen.fetchSize(() -> arrayValue.get().length);
                screen.valueFactory((id, i) -> {
                    Double[] arr = arrayValue.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = arrayValue.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<Double> attributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, Double.TYPE, setCallback, i));
                    return new DoubleValue(ValueData.of(attributes));
                });
                screen.addElement(() -> {
                    Double[] arr = arrayValue.get();
                    Double[] expanded = new Double[arr.length + 1];
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = Math.max(arrayValue.getRange().min(), 0);
                    arrayValue.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    Double[] arr = arrayValue.get();
                    arrayValue.setValue(trimmer.trim(i, arr, new Double[arr.length - 1]));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter stringArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            StringArrayValue arrayValue = (StringArrayValue) value;
            BiConsumer<String, Integer> setCallback = (val, i) -> {
                String[] arr = arrayValue.get();
                arr[i] = val;
                arrayValue.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<String, StringArrayValue> screen = new ArrayConfigScreen<>(holder, arrayValue, usedScreen);
                screen.fetchSize(() -> arrayValue.get().length);
                screen.valueFactory((id, i) -> {
                    String[] arr = arrayValue.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = arrayValue.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<String> attributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, String.class, setCallback, i));
                    return new StringValue(ValueData.of(attributes));
                });
                screen.addElement(() -> {
                    String[] arr = arrayValue.get();
                    String[] expanded = new String[arr.length + 1];
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = arrayValue.createElementInstance();
                    arrayValue.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    String[] arr = arrayValue.get();
                    arrayValue.setValue(trimmer.trim(i, arr, new String[arr.length - 1]));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter enumValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            return new EnumWidget<>(getValueX(x, width), y, getValueWidth(width), 20, (EnumValue<?>) value);
        });
    }

    @SuppressWarnings("unchecked")
    static <E extends Enum<E>> DisplayAdapter enumArrayValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            EnumArrayValue<E> enumArray = (EnumArrayValue<E>) value;
            BiConsumer<E, Integer> setCallback = (val, i) -> {
                E[] arr = enumArray.get();
                arr[i] = val;
                enumArray.setValue(arr);
            };
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen usedScreen = client.screen;
                ArrayConfigScreen<E, EnumArrayValue<E>> screen = new ArrayConfigScreen<>(holder, enumArray, usedScreen);
                screen.fetchSize(() -> enumArray.get().length);
                screen.valueFactory((id, i) -> {
                    E[] arr = enumArray.get();
                    TypeAdapter.TypeAttributes<?> parentAttributes = value.getValueData().getAttributes();
                    TypeAdapter.TypeAttributes<E> attributes = parentAttributes.child(id, arr[i], ArrayConfigScreen.callbackCtx(field, (Class<E>) enumArray.getValueType().getComponentType(), setCallback, i));
                    return new EnumValue<>(ValueData.of(attributes));
                });
                screen.addElement(() -> {
                    E[] arr = enumArray.get();
                    Class<E> type = (Class<E>) enumArray.getValueType().getComponentType();
                    E[] expanded = (E[]) Array.newInstance(type, arr.length + 1);
                    System.arraycopy(arr, 0, expanded, 0, arr.length);
                    expanded[arr.length] = type.getEnumConstants()[0];
                    enumArray.setValue(expanded);
                });
                screen.removeElement((i, trimmer) -> {
                    E[] arr = enumArray.get();
                    Class<E> type = (Class<E>) enumArray.getValueType().getComponentType();
                    enumArray.setValue(trimmer.trim(i, arr, (E[]) Array.newInstance(type, arr.length - 1)));
                });
                client.setScreen(screen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static DisplayAdapter objectValue() {
        return (holder, value, field, theme, container) -> container.addConfigWidget((x, y, width, height, configId) -> {
            ObjectValue objectValue = (ObjectValue) value;
            Map<String, ConfigValue<?>> valueMap = objectValue.get();
            Button.OnPress pressable = btn -> {
                Minecraft client = Minecraft.getInstance();
                Screen currentScreen = client.screen;
                Screen nestedConfigScreen = new ConfigScreen(holder, container.getComponentName(), valueMap, currentScreen);
                client.setScreen(nestedConfigScreen);
            };
            return Button.builder(ConfigEntryWidget.OPEN, pressable).pos(getValueX(x, width), y).size(getValueWidth(width), 20).build();
        });
    }

    static int getValueX(int x, int width) {
        return x + width - getValueWidth(width);
    }

    static int getValueWidth(int width) {
        return width / 3;
    }
}
