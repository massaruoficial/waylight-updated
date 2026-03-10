package com.soradotwav.waylight.render;

import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public final class LanternPoseController {
	private final LanternPoseState poseState = new LanternPoseState();
	private float lastYaw;

	public void tick(Minecraft client, VirtualLanternState lanternState) {
		poseState.prevSwayX = poseState.swayX;
		poseState.prevSwayZ = poseState.swayZ;
		poseState.prevBob = poseState.bob;

		LocalPlayer player = client.player;
		if (player == null || !lanternState.lightActive()) {
			decayToRest();
			return;
		}

		double horizontalSpeed = player.getDeltaMovement().horizontalDistance();
		float yawDelta = Mth.wrapDegrees(player.getYRot() - lastYaw);
		lastYaw = player.getYRot();

		float targetSwayX = Mth.clamp((float) (horizontalSpeed * 35.0D) - yawDelta * 0.35F, -18.0F, 18.0F);
		float targetSwayZ = Mth.clamp((float) player.getDeltaMovement().y() * -45.0F, -12.0F, 12.0F);
		float targetBob = (float) Math.sin(player.tickCount * 0.22F) * Mth.clamp((float) horizontalSpeed * 8.0F, 0.0F, 1.3F);

		poseState.swayX = Mth.lerp(0.2F, poseState.swayX, targetSwayX);
		poseState.swayZ = Mth.lerp(0.18F, poseState.swayZ, targetSwayZ);
		poseState.bob = Mth.lerp(0.22F, poseState.bob, targetBob);
	}

	public LanternPoseState getPoseState() {
		return poseState;
	}

	private void decayToRest() {
		poseState.swayX = Mth.lerp(0.2F, poseState.swayX, 0.0F);
		poseState.swayZ = Mth.lerp(0.2F, poseState.swayZ, 0.0F);
		poseState.bob = Mth.lerp(0.2F, poseState.bob, 0.0F);
	}
}
