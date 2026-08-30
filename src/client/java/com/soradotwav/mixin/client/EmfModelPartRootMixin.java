package com.soradotwav.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.soradotwav.waylight.render.EmfPlayerPoseBridge;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Optional EMF hook. No compile-time EMF dependency is required: @Pseudo and
 * the string target let the mod run normally when EMF is absent.
 *
 * Fresh Animations changes the EMF-owned left_arm ModelPart after vanilla
 * PlayerModel#setupAnim. Capture that *actual* final PartPose so the lantern
 * can use precisely the same attachment transform in third person.
 */
@Pseudo
@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartRoot", remap = false, priority = 850)
abstract class EmfModelPartRootMixin {
    @Unique
    private Method waylight$getAllVanillaParts;

    @Inject(method = "triggerManualAnimation(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("RETURN"), require = 0)
    private void waylight$captureFreshAnimationsLeftArm(PoseStack poseStack, CallbackInfo ci) {
        waylight$captureLeftArm();
    }

    @Inject(method = "animate()V", at = @At("RETURN"), require = 0)
    private void waylight$captureFreshAnimationsLeftArmAnimate(CallbackInfo ci) {
        waylight$captureLeftArm();
    }

    @Unique
    private void waylight$captureLeftArm() {
        try {
            Object self = this;
            if (waylight$getAllVanillaParts == null) {
                waylight$getAllVanillaParts = self.getClass().getMethod("getAllVanillaPartsEMF");
            }

            Object result = waylight$getAllVanillaParts.invoke(self);
            if (!(result instanceof Collection<?> parts)) return;

            for (Object candidate : parts) {
                if (!(candidate instanceof ModelPart modelPart)) continue;
                String text = String.valueOf(candidate);
                // EMFModelPartVanilla#toString() begins with "[vanilla part left_arm]".
                if (text.startsWith("[vanilla part left_arm]")) {
                    EmfPlayerPoseBridge.captureLeftArm(modelPart.storePose());
                    return;
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // EMF is optional and internal details can change. Failure here must
            // never make Waylight crash; the renderer falls back to vanilla.
        }
    }
}
