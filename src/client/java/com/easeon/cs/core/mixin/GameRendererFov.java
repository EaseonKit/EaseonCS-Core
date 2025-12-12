package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonFovClient;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererFov {
    @Inject(method = "getFov(Lnet/minecraft/client/Camera;FZ)F", at = @At("HEAD"), cancellable = true)
    private void onFovBefore(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        double dummyFov = 70.0;
        double modifiedFov = EaseonFovClient.onFovBefore(camera, tickDelta, changingFov, dummyFov);

        if (modifiedFov != dummyFov) {
            cir.setReturnValue((float)modifiedFov);
            cir.cancel();
        }
    }

    @Inject(method = "getFov(Lnet/minecraft/client/Camera;FZ)F", at = @At("RETURN"), cancellable = true)
    private void onFovAfter(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        float originalFov = cir.getReturnValue();

        // AFTER 이벤트 실행 - 원본 메서드가 계산한 FOV를 기반으로 수정
        double modifiedFov = EaseonFovClient.onFovAfter(camera, tickDelta, changingFov, (double)originalFov);

        if (modifiedFov != (double)originalFov) {
            cir.setReturnValue((float)modifiedFov);
        }
    }
}