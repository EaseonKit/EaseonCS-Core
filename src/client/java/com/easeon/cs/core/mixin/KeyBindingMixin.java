package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.keybind.EaseonKeyBindingRegistry;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Shadow
    private static Map<String, KeyBinding> KEYS_BY_ID;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onStaticInit(CallbackInfo ci) {
        // 등록된 키바인딩들을 Minecraft에 추가
        for (KeyBinding keyBinding : EaseonKeyBindingRegistry.getKeyBindings()) {
            KEYS_BY_ID.put(keyBinding.getBoundKeyTranslationKey(), keyBinding);
        }
    }
}