package com.soradotwav.mixin.client;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "punchy.client.state.HandEquipStateMachine", remap = false, priority = 1200)
public abstract class PunchyVisualOffhandMixin {
    @Inject(method = "getVisualOffhandStack", at = @At("HEAD"), cancellable = true, require = 0)
    private static void waylight$provideGhostLantern(
            LocalPlayer player,
            CallbackInfoReturnable<ItemStack> cir) {
        VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
        if (!state.enabled()
                || !state.modelVisible()
                || state.temporarilySuppressed()
                || state.lanternPosition() != LanternPosition.LEFT_HAND) {
            return;
        }

        Item item = state.lanternType() == LanternType.SOUL ? Items.SOUL_LANTERN : Items.LANTERN;
        cir.setReturnValue(item.getDefaultInstance());
    }
}
