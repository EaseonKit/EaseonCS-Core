package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonHudRenderClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.Gui.class)
public class InGameHud {
    @Inject(method = "render", at = @At("HEAD"))
    private void onHudRenderBefore(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        EaseonHudRenderClient.onHudRenderBefore(guiGraphics, deltaTracker);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onHudRenderAfter(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        EaseonHudRenderClient.onHudRenderAfter(guiGraphics, deltaTracker);
    }
}