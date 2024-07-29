package dev.toma.configuration.config.validate;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AggregatedValidationResult implements IValidationResult {

    public static final Component CHILD_VALUE_WARNING = Component.translatable("text.configuration.validation.child_failed.warning");
    public static final Component CHILD_VALUE_ERROR = Component.translatable("text.configuration.validation.child_failed.error");

    private final IValidationResult.Severity severity;
    private final List<Component> messages;

    private AggregatedValidationResult(IValidationResult.Severity severity, List<Component> messages) {
        this.severity = severity;
        this.messages = Collections.unmodifiableList(messages);
    }

    public static AggregatedValidationResult joinChild(AggregatedValidationResult r1, AggregatedValidationResult r2) {
        List<Component> messages = new ArrayList<>();
        Severity s1 = r1 != null ? r1.severity() : Severity.NONE;
        Severity s2 = r2 != null ? r2.severity() : Severity.NONE;
        Severity result = s1.isHigherSeverityThan(s2) ? s1 : s2;
        if (result == Severity.NONE) {
            return new AggregatedValidationResult(Severity.NONE, Collections.emptyList());
        }
        if (r1 != null) {
            messages.addAll(r1.messages);
        }
        if (r2 != null && s2.isWarningOrError()) {
            messages.add(CommonComponents.EMPTY);
            messages.add(s2 == Severity.WARNING ? CHILD_VALUE_WARNING : CHILD_VALUE_ERROR);
        }
        return new AggregatedValidationResult(result, messages);
    }

    public static AggregatedValidationResult aggregate(List<IValidationResult> results) {
        IValidationResult.Severity severity = IValidationResult.Severity.NONE;
        List<Component> messages = new ArrayList<>();
        if (!results.isEmpty()) {
            for (IValidationResult result : results) {
                List<Component> resultMessages = result.messages().stream().filter(c -> c != null && !c.equals(CommonComponents.EMPTY)).toList();
                IValidationResult.Severity resultSeverity = result.severity();
                if (resultSeverity.isHigherSeverityThan(severity)) {
                    messages.addAll(resultMessages);
                    messages.add(CommonComponents.EMPTY);
                    severity = resultSeverity;
                }
            }
        }
        return new AggregatedValidationResult(severity, messages);
    }

    public boolean isValid() {
        return this.severity.isValid();
    }

    @Override
    public IValidationResult.Severity severity() {
        return severity;
    }

    @Override
    public List<Component> messages() {
        return messages;
    }
}
