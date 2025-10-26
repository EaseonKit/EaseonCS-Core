package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonBlockUseClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientInteractBlockUse {
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onBlockInteractBefore(ClientPlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = EaseonBlockUseClient.onBlockUseBefore(player, player.getEntityWorld(), hand, hit);
        if (result == ActionResult.CONSUME || result == ActionResult.FAIL) {
            cir.setReturnValue(result);
            cir.cancel();
        }
        else if (result == ActionResult.SUCCESS) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "interactBlock", at = @At("TAIL"))
    private void onBlockInteractAfter(ClientPlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = EaseonBlockUseClient.onBlockUseAfter(player, player.getEntityWorld(), hand, hit);
        if (result != ActionResult.PASS) {
            cir.setReturnValue(result);
        }
    }
}