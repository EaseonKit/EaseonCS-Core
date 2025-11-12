package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonHudRenderClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public class InGameHud {
    @Inject(method = "render", at = @At("HEAD"))
    private void onHudRenderBefore(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EaseonHudRenderClient.onHudRenderBefore(context, tickCounter);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onHudRenderAfter(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EaseonHudRenderClient.onHudRenderAfter(context, tickCounter);
    }
}