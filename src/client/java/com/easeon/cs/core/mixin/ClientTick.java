package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonKeyBindClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientTick {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onEndClientTick(CallbackInfo ci) {
        EaseonKeyBindClient.onEndClientTick((Minecraft)(Object)this);
    }
}