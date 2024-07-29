package dev.toma.configuration.config.validate;

import net.minecraft.network.chat.Component;

import java.util.List;

public record ValidationResultHolder(IValidationResult.Severity severity, List<Component> messages) {
}
