package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.keybind.EaseonKeyBindingRegistry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow
    @Final
    public KeyBinding[] allKeys;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        // 커스텀 키바인딩을 allKeys 배열에 추가
        java.util.List<KeyBinding> customKeys = EaseonKeyBindingRegistry.getKeyBindings();
        if (!customKeys.isEmpty()) {
            KeyBinding[] newAllKeys = new KeyBinding[allKeys.length + customKeys.size()];
            System.arraycopy(allKeys, 0, newAllKeys, 0, allKeys.length);
            for (int i = 0; i < customKeys.size(); i++) {
                newAllKeys[allKeys.length + i] = customKeys.get(i);
            }
            try {
                java.lang.reflect.Field field = GameOptions.class.getDeclaredField("allKeys");
                field.setAccessible(true);
                field.set(this, newAllKeys);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}