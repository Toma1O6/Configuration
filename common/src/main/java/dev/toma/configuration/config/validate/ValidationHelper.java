package dev.toma.configuration.config.validate;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ValidationHelper {

    public static final Component CHILD_VALUE_WARNING = Component.translatable("text.configuration.validation.child_failed.warning");
    public static final Component CHILD_VALUE_ERROR = Component.translatable("text.configuration.validation.child_failed.error");

    private ValidationHelper() {
    }

    public static ValidationResult joinChild(ValidationResult r1, ValidationResult r2) {
        List<Component> messages = new ArrayList<>();
        ValidationResult.Type s1 = r1 != null ? r1.type() : ValidationResult.Type.SUCCESS;
        ValidationResult.Type s2 = r2 != null ? r2.type() : ValidationResult.Type.SUCCESS;
        ValidationResult.Type result = s1.isMoreSevereThan(s2) ? s1 : s2;
        if (result == ValidationResult.Type.SUCCESS) {
            return ValidationResult.success();
        }
        if (r1 != null) {
            messages.addAll(r1.description());
        }
        if (r2 != null && s2.isWarningOrError()) {
            messages.add(CommonComponents.EMPTY);
            messages.add(s2 == ValidationResult.Type.WARNING ? CHILD_VALUE_WARNING : CHILD_VALUE_ERROR);
        }
        return new ValidationResult(result, messages);
    }

    public static ValidationResult aggregate(List<ValidationResult> results) {
        ValidationResult.Type type = ValidationResult.Type.SUCCESS;
        List<Component> messages = new ArrayList<>();
        if (!results.isEmpty()) {
            for (ValidationResult result : results) {
                List<Component> resultMessages = result.description().stream().filter(c -> c != null && !c.equals(CommonComponents.EMPTY)).toList();
                ValidationResult.Type resultType = result.type();
                if (resultType.isMoreSevereThan(type)) {
                    messages.addAll(resultMessages);
                    messages.add(CommonComponents.EMPTY);
                    type = resultType;
                }
            }
        }
        return new ValidationResult(type, messages);
    }
}
