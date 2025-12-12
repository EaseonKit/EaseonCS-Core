package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonMouseScrollClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScrollBefore(long window, double horizontal, double vertical, CallbackInfo ci) {
        boolean shouldCancel = EaseonMouseScrollClient.onMouseScrollBefore(window, horizontal, vertical);

        if (shouldCancel) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("RETURN"))
    private void onMouseScrollAfter(long window, double horizontal, double vertical, CallbackInfo ci) {
        EaseonMouseScrollClient.onMouseScrollAfter(window, horizontal, vertical);
    }
}