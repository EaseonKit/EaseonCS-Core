package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonScreenInitClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
    protected Minecraft minecraft;

    @Inject(method = "init", at = @At("HEAD"))
    private void onScreenInitBefore(int width, int height, CallbackInfo ci) {
        EaseonScreenInitClient.onScreenInitBefore(this.minecraft, (Screen)(Object)this, width, height);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onScreenInitAfter(int width, int height, CallbackInfo ci) {
        EaseonScreenInitClient.onScreenInitAfter(this.minecraft, (Screen)(Object)this, width, height);
    }
}