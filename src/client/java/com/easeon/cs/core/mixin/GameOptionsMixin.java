package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.keybind.EaseonKeyBindingRegistry;
import net.minecraft.client.Options;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class GameOptionsMixin {

    @Shadow
    @Final
    @Mutable
    public KeyMapping[] keyMappings;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        java.util.List<KeyMapping> customKeys = EaseonKeyBindingRegistry.getKeyBindings();
        if (!customKeys.isEmpty()) {
            KeyMapping[] newAllKeys = new KeyMapping[keyMappings.length + customKeys.size()];
            System.arraycopy(keyMappings, 0, newAllKeys, 0, keyMappings.length);
            for (int i = 0; i < customKeys.size(); i++) {
                newAllKeys[keyMappings.length + i] = customKeys.get(i);
            }
            keyMappings = newAllKeys;
        }
    }
}