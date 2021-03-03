package dev.toma.configuration.api.client;

import dev.toma.configuration.api.client.component.*;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This interface is responsible for creating {@link Component} instances in UI
 *
 * @author Toma
 */
@SideOnly(Side.CLIENT)
public interface ComponentFactory {

    ComponentFactory BOOLEAN = (screen, t, x, y, width, height) -> {
        BooleanType type = (BooleanType) t;
        String key = type.getId();
        if(key.isEmpty()) {
            screen.addComponent(new BooleanComponent(type, x, y, width, height));
        } else {
            int nameEnd = width / 2;
            int typeWidth = width - nameEnd;
            screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
            screen.addComponent(new BooleanComponent(type, x + nameEnd, y, typeWidth, height));
        }
    };
    ComponentFactory INTEGER = (screen, t, x, y, width, height) -> {
        IntType intType = (IntType) t;
        String key = intType.getId();
        switch (intType.getDisplayType()) {
            case TEXT_FIELD:
                if(key.isEmpty()) {
                    screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x, y, width, height).blockErrors());
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), intType.getId()));
                    screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x + nameEnd, y, typeWidth, height));
                }
                break;
            case SLIDER:
                if(key.isEmpty()) {
                    screen.addComponent(new SliderComponent<>(screen, intType, true, x, y, width, height, IntType::setFromSlider));
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), intType.getId()));
                    screen.addComponent(new SliderComponent<>(screen, intType, true, x + nameEnd, y, typeWidth, height, IntType::setFromSlider));
                }
                break;
            case TEXT_FIELD_SLIDER:
                if(key.isEmpty()) {
                    int half = width / 2;
                    int left = width - half;
                    SliderComponent<IntType> slider = screen.addComponent(new SliderComponent<>(screen, intType, false, x, y, half - 5, height, IntType::setFromSlider));
                    TextFieldComponent.IntegerField field = screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x + half, y, left, height));
                    slider.addListener(type -> field.displayedText = type.get().toString());
                    field.addListener(type -> slider.updatePosition());
                } else {
                    int _13 = width / 3;
                    screen.addComponent(new PlainTextComponent(x, y, _13, height, screen.getTextColor(), intType.getId()));
                    SliderComponent<IntType> slider = screen.addComponent(new SliderComponent<>(screen, intType, false, x + _13, y, _13 - 5, height, IntType::setFromSlider));
                    TextFieldComponent.IntegerField field = screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x + 2 * _13, y, width - 2 * _13, height));
                    slider.addListener(type -> field.displayedText = type.get().toString());
                    field.addListener(type -> slider.updatePosition());
                }
                break;
        }
    };
    ComponentFactory DECIMAL = (screen, t, x, y, width, height) -> {
        DoubleType type = (DoubleType) t;
        String key = type.getId();
        switch (type.getDisplayType()) {
            case TEXT_FIELD:
                if(key.isEmpty()) {
                    screen.addComponent(new TextFieldComponent.DecimalField(screen, type, x, y, width, height).blockErrors());
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new TextFieldComponent.DecimalField(screen, type, x + nameEnd, y, typeWidth, height));
                }
                break;
            case SLIDER:
                if(key.isEmpty()) {
                    screen.addComponent(new SliderComponent<>(screen, type, true, x, y, width, height, DoubleType::setFromSlider));
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new SliderComponent<>(screen, type, true, x + nameEnd, y, typeWidth, height, DoubleType::setFromSlider));
                }
                break;
            case TEXT_FIELD_SLIDER:
                if(key.isEmpty()) {
                    int half = width / 2;
                    int left = width - half;
                    SliderComponent<DoubleType> slider = screen.addComponent(new SliderComponent<>(screen, type, false, x, y, half - 5, height, DoubleType::setFromSlider));
                    TextFieldComponent.DecimalField field = screen.addComponent(new TextFieldComponent.DecimalField(screen, type, x + half, y, left, height));
                    slider.addListener(ct -> field.displayedText = ct.get().toString());
                    field.addListener(ct -> slider.updatePosition());
                } else {
                    int _13 = width / 3;
                    screen.addComponent(new PlainTextComponent(x, y, _13, height, screen.getTextColor(), type.getId()));
                    SliderComponent<DoubleType> slider = screen.addComponent(new SliderComponent<>(screen, type, false, x + _13, y, _13 - 5, height, DoubleType::setFromSlider));
                    TextFieldComponent.DecimalField field = screen.addComponent(new TextFieldComponent.DecimalField(screen, type, x + 2 * _13, y, width - 2 * _13, height));
                    slider.addListener(ct -> field.displayedText = ct.get().toString());
                    field.addListener(ct -> slider.updatePosition());
                }
                break;
        }
    };
    ComponentFactory STRING = (screen, t, x, y, width, height) -> {
        StringType type = (StringType) t;
        String key = type.getId();
        if(key.isEmpty()) {
            screen.addComponent(new TextFieldComponent.StringField(screen, type, x, y, width, height).blockErrors());
        } else {
            int nameEnd = width / 2;
            int typeWidth = width - nameEnd;
            screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
            screen.addComponent(new TextFieldComponent.StringField(screen, type, x + nameEnd, y, typeWidth, height));
        }
    };
    ComponentFactory ARRAY = (screen, t, x, y, width, height) -> {
        FixedCollectionType<?> type = (FixedCollectionType<?>) t;
        String key = type.getId();
        if(key.isEmpty()) {
            screen.addComponent(new ArrayComponent<>(type, x, y, width, height));
        } else {
            int nameEnd = width / 2;
            int typeWidth = width - nameEnd;
            screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
            screen.addComponent(new ArrayComponent<>(type, x + nameEnd, y, typeWidth, height));
        }
    };
    ComponentFactory OBJECT = (screen, type, x, y, width, height) -> screen.addComponent(new ObjectTypeComponent(screen, (ObjectType) type, x, y, width, height));
    ComponentFactory COLLECTION = (screen, type, x, y, width, height) -> screen.addComponent(new CollectionComponent<>(screen, (CollectionType<?>) type, x, y, width, height));
    ComponentFactory COLOR = (screen, t, x, y, width, height) -> {
        ColorType type = (ColorType) t;
        int half = width / 2;
        int left = width - half;
        screen.addComponent(new PlainTextComponent(x, y, half - 20, height, screen.getTextColor(), type.getId()));
        ColorDisplayComponent color = screen.addComponent(new ColorDisplayComponent(type, x + half - 19, y, 20, height));
        TextFieldComponent.StringField field = screen.addComponent(new TextFieldComponent.StringField(screen, type, x + half, y, left, height));
        field.addListener(ct -> color.refresh());
    };

    void addComponents(ComponentScreen screen, AbstractConfigType<?> type, int x, int y, int width, int height);
}
