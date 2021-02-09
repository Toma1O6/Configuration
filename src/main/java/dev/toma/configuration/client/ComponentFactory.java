package dev.toma.configuration.client;

import dev.toma.configuration.api.type.*;
import dev.toma.configuration.client.screen.ComponentScreen;
import dev.toma.configuration.client.screen.component.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ComponentFactory {

    ComponentFactory BOOLEAN = (screen, type, x, y, width, height) -> {
        String key = type.getId();
        if(key.isEmpty()) {
            screen.addComponent(new BooleanComponent((BooleanType) type, x, y, width, height));
        } else {
            int nameEnd = width / 2;
            int typeWidth = width - nameEnd;
            screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
            screen.addComponent(new BooleanComponent((BooleanType) type, x + nameEnd, y, typeWidth, height));
        }
    };
    ComponentFactory INTEGER = (screen, type, x, y, width, height) -> {
        String key = type.getId();
        IntType intType = (IntType) type;
        switch (intType.getDisplayType()) {
            case TEXT_FIELD:
                if(key.isEmpty()) {
                    screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x, y, width, height));
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x + nameEnd, y, typeWidth, height));
                }
                break;
            case SLIDER:
                if(key.isEmpty()) {
                    screen.addComponent(new SliderComponent<>(screen, intType, true, x, y, width, height, IntType::setFromSlider));
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new SliderComponent<>(screen, intType, true, x + nameEnd, y, typeWidth, height, IntType::setFromSlider));
                }
                break;
            case TEXT_FIELD_SLIDER:
                if(key.isEmpty()) {
                    int half = width / 2;
                    int left = width - half;
                    screen.addComponent(new SliderComponent<>(screen, intType, false, x, y, half - 5, height, IntType::setFromSlider));
                    screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x + half, y, left, height));
                } else {
                    int _13 = width / 3;
                    screen.addComponent(new PlainTextComponent(x, y, _13, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new SliderComponent<>(screen, intType, false, x + _13, y, _13 - 5, height, IntType::setFromSlider));
                    screen.addComponent(new TextFieldComponent.IntegerField(screen, intType, x + 2 * _13, y, width - 2 * _13, height));
                }
                break;
        }
    };
    ComponentFactory DECIMAL = (screen, type, x, y, width, height) -> {
        String key = type.getId();
        DoubleType doubleType = (DoubleType) type;
        switch (doubleType.getDisplayType()) {
            case TEXT_FIELD:
                if(key.isEmpty()) {
                    screen.addComponent(new TextFieldComponent.DecimalField(screen, doubleType, x, y, width, height));
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new TextFieldComponent.DecimalField(screen, doubleType, x + nameEnd, y, typeWidth, height));
                }
                break;
            case SLIDER:
                if(key.isEmpty()) {
                    screen.addComponent(new SliderComponent<>(screen, doubleType, true, x, y, width, height, DoubleType::setFromSlider));
                } else {
                    int nameEnd = width / 2;
                    int typeWidth = width - nameEnd;
                    screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new SliderComponent<>(screen, doubleType, true, x + nameEnd, y, typeWidth, height, DoubleType::setFromSlider));
                }
                break;
            case TEXT_FIELD_SLIDER:
                if(key.isEmpty()) {
                    int half = width / 2;
                    int left = width - half;
                    screen.addComponent(new SliderComponent<>(screen, doubleType, false, x, y, half - 5, height, DoubleType::setFromSlider));
                    screen.addComponent(new TextFieldComponent.DecimalField(screen, doubleType, x + half, y, left, height));
                } else {
                    int _13 = width / 3;
                    screen.addComponent(new PlainTextComponent(x, y, _13, height, screen.getTextColor(), type.getId()));
                    screen.addComponent(new SliderComponent<>(screen, doubleType, false, x + _13, y, _13 - 5, height, DoubleType::setFromSlider));
                    screen.addComponent(new TextFieldComponent.DecimalField(screen, doubleType, x + 2 * _13, y, width - 2 * _13, height));
                }
                break;
        }
    };
    ComponentFactory STRING = (screen, type, x, y, width, height) -> {
        String key = type.getId();
        if(key.isEmpty()) {
            screen.addComponent(new TextFieldComponent.StringField(screen, (StringType) type, x, y, width, height));
        } else {
            int nameEnd = width / 2;
            int typeWidth = width - nameEnd;
            screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
            screen.addComponent(new TextFieldComponent.StringField(screen, (StringType) type, x + nameEnd, y, typeWidth, height));
        }
    };
    ComponentFactory MULTI_CHOICE = (screen, type, x, y, width, height) -> {
        String key = type.getId();
        if(key.isEmpty()) {
            screen.addComponent(new ArrayComponent<>((FixedCollectionType<?>) type, x, y, width, height));
        } else {
            int nameEnd = width / 2;
            int typeWidth = width - nameEnd;
            screen.addComponent(new PlainTextComponent(x, y, nameEnd, height, screen.getTextColor(), type.getId()));
            screen.addComponent(new ArrayComponent<>((FixedCollectionType<?>) type, x + nameEnd, y, typeWidth, height));
        }
    };
    ComponentFactory OBJECT = (screen, type, x, y, width, height) -> screen.addComponent(new ObjectTypeComponent(screen, (ObjectType) type, x, y, width, height));
    ComponentFactory COLLECTION = (screen, type, x, y, width, height) -> screen.addComponent(new CollectionComponent<>(screen, (CollectionType<?>) type, x, y, width, height));
    ComponentFactory COLOR = (screen, type, x, y, width, height) -> {
        int half = width / 2;
        int left = width - half;
        ColorType colorType = (ColorType) type;
        screen.addComponent(new PlainTextComponent(x, y, half - 20, height, screen.getTextColor(), type.getId()));
        screen.addComponent(new TextFieldComponent.StringField(screen, colorType, x + half, y, left, height));
        screen.addComponent(new ColorDisplayComponent(colorType, x + half - 19, y, 20, height));
    };

    void addComponents(ComponentScreen screen, AbstractConfigType<?> type, int x, int y, int width, int height);
}
