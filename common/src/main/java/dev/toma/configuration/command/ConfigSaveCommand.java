package dev.toma.configuration.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ConfigSaveCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = new ConfigSuggestionProvider();
    private static final SimpleCommandExceptionType NOT_ENOUGH_ARGUMENTS = new SimpleCommandExceptionType(Component.translatable("text.configuration.command.not_enough_arguments"));
    private static final DynamicCommandExceptionType UNKNOWN_CONFIG = new DynamicCommandExceptionType(t -> Component.translatable("text.configuration.command.unknown_config", t));
    private static final Component TEXT_CONFIGS_SAVED = Component.translatable("text.configuration.command.configs_saved").withStyle(ChatFormatting.GREEN);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("configuration")
                        .requires(stack -> stack.hasPermission(2))
                        .executes(stack -> {
                            throw NOT_ENOUGH_ARGUMENTS.create();
                        })
                        .then(
                                literal("saveAll")
                                        .executes(context -> saveConfig(context, true))
                        )
                        .then(
                                literal("save")
                                        .executes(stack -> {
                                            throw NOT_ENOUGH_ARGUMENTS.create();
                                        })
                                        .then(
                                                argument("configId", StringArgumentType.greedyString())
                                                        .suggests(SUGGESTION_PROVIDER)
                                                        .executes(context -> saveConfig(context, false))
                                        )
                        )
        );
    }

    private static int saveConfig(CommandContext<CommandSourceStack> ctx, boolean isSaveAll) throws CommandSyntaxException {
        List<String> configsForUpdate = isSaveAll ? new ArrayList<>(ConfigHolder.getRegisteredConfigs()) : Collections.singletonList(ctx.getArgument("configId", String.class));
        for (String configId : configsForUpdate) {
            ConfigHolder<?> holder = Configuration.getConfig(configId).orElseThrow(() -> UNKNOWN_CONFIG.create(configId));
            holder.save();
        }
        ctx.getSource().sendSuccess(() -> TEXT_CONFIGS_SAVED, true);
        return 0;
    }

    private static class ConfigSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            Collection<String> configs = ConfigHolder.getRegisteredConfigs();
            configs.forEach(builder::suggest);
            return builder.buildFuture();
        }
    }
}
