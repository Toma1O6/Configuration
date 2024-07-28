package dev.toma.configuration.mixin;

import com.mojang.blaze3d.platform.WindowEventHandler;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.io.ConfigIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {

    public MinecraftMixin(String name) {
        super(name);
    }

    @Inject(
            method = "clearClientLevel",
            at = @At("RETURN")
    )
    private void configuration$reloadClientConfigs(Screen screen, CallbackInfo ci) {
        ConfigIO.setEnvironment(ConfigIO.ConfigEnvironment.MENU);
        ConfigHolder.getSynchronizedConfigs().stream()
                .map(ConfigHolder::getConfig)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(ConfigHolder::restoreClientStoredValues);
    }

    @Inject(
            method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V",
            at = @At("RETURN")
    )
    private void configuration$disconnect(Screen screen, boolean canceled, CallbackInfo ci) {
        ConfigIO.setEnvironment(ConfigIO.ConfigEnvironment.MENU);
        ConfigHolder.getSynchronizedConfigs().stream()
                .map(ConfigHolder::getConfig)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(ConfigHolder::restoreClientStoredValues);
    }

    @Inject(
            method = "onGameLoadFinished",
            at = @At("RETURN")
    )
    private void configuration$onGameLoadFinished(CallbackInfo ci) {
        ConfigIO.setEnvironment(ConfigIO.ConfigEnvironment.MENU);
    }
}
