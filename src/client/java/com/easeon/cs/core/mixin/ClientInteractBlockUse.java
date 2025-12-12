package com.easeon.cs.core.mixin;

import com.easeon.cs.core.api.events.EaseonBlockUseClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ClientInteractBlockUse {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onBlockInteractBefore(LocalPlayer player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = EaseonBlockUseClient.onBlockUseBefore(player, player.level(), hand, hit);
        if (result == InteractionResult.CONSUME || result == InteractionResult.FAIL) {
            cir.setReturnValue(result);
            cir.cancel();
        }
        else if (result == InteractionResult.SUCCESS) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "useItemOn", at = @At("TAIL"))
    private void onBlockInteractAfter(LocalPlayer player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = EaseonBlockUseClient.onBlockUseAfter(player, player.level(), hand, hit);
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(result);
        }
    }
}