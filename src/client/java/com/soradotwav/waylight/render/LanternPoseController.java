package com.soradotwav.waylight.render;

import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public final class LanternPoseController {
	private static final float PITCH_STIFFNESS = 0.12F;
	private static final float ROLL_STIFFNESS = 0.1F;
	private static final float PITCH_DAMPING = 0.86F;
	private static final float ROLL_DAMPING = 0.84F;
	private static final float YAW_LAG_DAMPING = 0.8F;
	private static final float MAX_PITCH = 28.0F;
	private static final float MAX_ROLL = 34.0F;
	private static final float MAX_YAW_LAG = 18.0F;
	private static final float PITCH_ACCELERATION_SCALE = 120.0F;
	private static final float ROLL_ACCELERATION_SCALE = 150.0F;
	private static final float TURN_SCALE = 0.32F;

	private final LanternPoseState poseState = new LanternPoseState();
	private float lastYaw;
	private double lastVelocityX;
	private double lastVelocityY;
	private double lastVelocityZ;

	public void tick(Minecraft client, VirtualLanternState lanternState) {
		poseState.prevPitchAngle = poseState.pitchAngle;
		poseState.prevRollAngle = poseState.rollAngle;
		poseState.prevYawLag = poseState.yawLag;
		poseState.prevBob = poseState.bob;

		LocalPlayer player = client.player;
		if (player == null || !lanternState.lightActive()) {
			decayToRest();
			return;
		}

		var velocity = player.getDeltaMovement();
		double accelerationX = velocity.x - lastVelocityX;
		double accelerationY = velocity.y - lastVelocityY;
		double accelerationZ = velocity.z - lastVelocityZ;
		double horizontalSpeed = velocity.horizontalDistance();
		float yawDelta = Mth.wrapDegrees(player.getYRot() - lastYaw);

		lastVelocityX = velocity.x;
		lastVelocityY = velocity.y;
		lastVelocityZ = velocity.z;
		lastYaw = player.getYRot();

		float forwardAcceleration = (float) (-Math.sin(Math.toRadians(player.getYRot())) * accelerationX + Math.cos(Math.toRadians(player.getYRot())) * accelerationZ);
		float sidewaysAcceleration = (float) (Math.cos(Math.toRadians(player.getYRot())) * accelerationX + Math.sin(Math.toRadians(player.getYRot())) * accelerationZ);
		float targetBob = (float) Math.sin(player.tickCount * 0.22F) * Mth.clamp((float) horizontalSpeed * 8.0F, 0.0F, 1.3F);

		poseState.pitchVelocity += (-forwardAcceleration * PITCH_ACCELERATION_SCALE) + ((float) accelerationY * -14.0F);
		poseState.rollVelocity += (sidewaysAcceleration * ROLL_ACCELERATION_SCALE) - (yawDelta * TURN_SCALE);

		poseState.pitchVelocity += -poseState.pitchAngle * PITCH_STIFFNESS;
		poseState.rollVelocity += -poseState.rollAngle * ROLL_STIFFNESS;

		poseState.pitchVelocity *= PITCH_DAMPING;
		poseState.rollVelocity *= ROLL_DAMPING;

		poseState.pitchAngle = Mth.clamp(poseState.pitchAngle + poseState.pitchVelocity, -MAX_PITCH, MAX_PITCH);
		poseState.rollAngle = Mth.clamp(poseState.rollAngle + poseState.rollVelocity, -MAX_ROLL, MAX_ROLL);
		poseState.yawLag = Mth.clamp((poseState.yawLag + yawDelta * 0.18F) * YAW_LAG_DAMPING, -MAX_YAW_LAG, MAX_YAW_LAG);
		poseState.bob = Mth.lerp(0.22F, poseState.bob, targetBob);
	}

	public LanternPoseState getPoseState() {
		return poseState;
	}

	private void decayToRest() {
		lastVelocityX = 0.0;
		lastVelocityY = 0.0;
		lastVelocityZ = 0.0;
		poseState.pitchVelocity *= 0.75F;
		poseState.rollVelocity *= 0.75F;
		poseState.pitchAngle = Mth.lerp(0.2F, poseState.pitchAngle, 0.0F);
		poseState.rollAngle = Mth.lerp(0.2F, poseState.rollAngle, 0.0F);
		poseState.yawLag = Mth.lerp(0.2F, poseState.yawLag, 0.0F);
		poseState.bob = Mth.lerp(0.2F, poseState.bob, 0.0F);
	}
}
