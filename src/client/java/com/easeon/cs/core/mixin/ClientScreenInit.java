package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonScreenInitClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ClientScreenInit {

    @Shadow
    protected int width;

    @Shadow
    protected int height;

    @Shadow
    protected MinecraftClient client;

    // Screen.init(MinecraftClient, int, int) 메서드를 타겟으로 합니다
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    private void onScreenInitBefore(MinecraftClient client, int width, int height, CallbackInfo ci) {
        EaseonScreenInitClient.onScreenInitBefore(client, (Screen)(Object)this, width, height);
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    private void onScreenInitAfter(MinecraftClient client, int width, int height, CallbackInfo ci) {
        EaseonScreenInitClient.onScreenInitAfter(client, (Screen)(Object)this, width, height);
    }
}