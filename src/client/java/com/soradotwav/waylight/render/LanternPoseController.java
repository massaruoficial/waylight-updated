package com.soradotwav.waylight.render;

import com.soradotwav.waylight.lantern.VirtualLanternState;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.WaylightClient;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public final class LanternPoseController {
	private static final float HIP_THIRD_PERSON_MOTION_SCALE = 0.48F;
	private static final float HAND_LEFT_THIRD_PERSON_MOTION_SCALE = 0.72F;
	private static final float PITCH_STIFFNESS = 0.096F;
	private static final float ROLL_STIFFNESS = 0.08F;
	private static final float PITCH_DAMPING = 0.89F;
	private static final float ROLL_DAMPING = 0.88F;
	private static final float YAW_LAG_DAMPING = 0.84F;
	private static final float MAX_PITCH = 86.0F;
	private static final float MAX_ROLL = 34.0F;
	private static final float MAX_YAW_LAG = 18.0F;
	private static final float PITCH_ACCELERATION_SCALE = 62.4F;
	private static final float ROLL_ACCELERATION_SCALE = 70.8F;
	private static final float FALL_PITCH_BIAS_SCALE = 78.0F;
	private static final float MAX_FALL_PITCH_TARGET = 84.0F;
	private static final float FALL_PITCH_STIFFNESS = 0.16F;
	private static final float TURN_SCALE = 0.132F;
	private static final float SPRINT_MOTION_MULTIPLIER = 1.0F;
	private static final float CROUCH_MOTION_MULTIPLIER = 0.65F;
	private static final float CROUCH_DAMPING_BONUS = 0.08F;
	private static final float JUMP_PITCH_IMPULSE = -3.15F;
	private static final float AIR_MOTION_MULTIPLIER = 0.72F;
	private static final float LANDING_PITCH_IMPULSE_SCALE = 5.1F;
	private static final float MAX_LANDING_PITCH_IMPULSE = 5.4F;
	private static final float LANDING_ROLL_DAMPING = 0.85F;
	private static final float HAND_LEFT_MOTION_SCALE = 0.75F;
	private static final float HAND_LEFT_FALL_SCALE = 0.75F;
	private static final float HAND_LEFT_YAW_SCALE = 0.75F;
	private static final float HAND_LEFT_BOB_SCALE = 0.75F;

	private final LanternPoseState poseState = new LanternPoseState();
	private float lastYaw;
	private double lastVelocityX;
	private double lastVelocityY;
	private double lastVelocityZ;
	private boolean wasOnGround;

	public void tick(Minecraft client, VirtualLanternState lanternState) {
		poseState.prevPitchAngle = poseState.pitchAngle;
		poseState.prevRollAngle = poseState.rollAngle;
		poseState.prevYawLag = poseState.yawLag;
		poseState.prevBob = poseState.bob;

		LocalPlayer player = client.player;
		if (player == null || !lanternState.enabled()) {
			decayToRest();
			return;
		}

		var velocity = player.getDeltaMovement();
		double accelerationX = velocity.x - lastVelocityX;
		double accelerationY = velocity.y - lastVelocityY;
		double accelerationZ = velocity.z - lastVelocityZ;
		double horizontalSpeed = velocity.horizontalDistance();
		float yawDelta = Mth.wrapDegrees(player.getYRot() - lastYaw);
		boolean onGround = player.onGround();
		boolean sprinting = player.isSprinting();
		boolean crouching = player.isCrouching();
		boolean firstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;
		boolean hipThirdPerson = lanternState.lanternPosition().isHipMounted() && !firstPerson;
		boolean handLeftThirdPerson = lanternState.lanternPosition() == LanternPosition.LEFT_HAND && !firstPerson;

		lastVelocityX = velocity.x;
		lastVelocityY = velocity.y;
		lastVelocityZ = velocity.z;
		lastYaw = player.getYRot();

		float forwardAcceleration = (float) (-Math.sin(Math.toRadians(player.getYRot())) * accelerationX + Math.cos(Math.toRadians(player.getYRot())) * accelerationZ);
		float sidewaysAcceleration = (float) (Math.cos(Math.toRadians(player.getYRot())) * accelerationX + Math.sin(Math.toRadians(player.getYRot())) * accelerationZ);
		float modeMotionScale = hipThirdPerson
			? HIP_THIRD_PERSON_MOTION_SCALE
			: handLeftThirdPerson ? HAND_LEFT_THIRD_PERSON_MOTION_SCALE : 1.0F;
		float targetBob = (float) Math.sin(player.tickCount * 0.22F) * Mth.clamp((float) horizontalSpeed * 8.0F, 0.0F, 1.3F) * modeMotionScale;
		float motionIntensity = Mth.clamp(WaylightClient.CONFIG_MANAGER.get().motionIntensity / 100.0F, 0.25F, 2.0F);
		boolean handLeftFirstPerson = lanternState.lanternPosition() == LanternPosition.LEFT_HAND && firstPerson;
		float motionMultiplier = sprinting ? SPRINT_MOTION_MULTIPLIER : crouching ? CROUCH_MOTION_MULTIPLIER : 1.0F;
		if (!onGround) {
			motionMultiplier *= AIR_MOTION_MULTIPLIER;
		}
		motionMultiplier *= motionIntensity * modeMotionScale;
		if (handLeftFirstPerson) {
			motionMultiplier *= HAND_LEFT_MOTION_SCALE;
		}
		float pitchDamping = crouching ? PITCH_DAMPING + CROUCH_DAMPING_BONUS : PITCH_DAMPING;
		float rollDamping = crouching ? ROLL_DAMPING + CROUCH_DAMPING_BONUS : ROLL_DAMPING;
		float fallPitchTarget = 0.0F;

		if (!onGround && velocity.y < -0.08) {
			fallPitchTarget = -Mth.clamp((float) (-velocity.y) * FALL_PITCH_BIAS_SCALE * motionIntensity * modeMotionScale, 0.0F, MAX_FALL_PITCH_TARGET);
			if (handLeftFirstPerson) {
				fallPitchTarget *= HAND_LEFT_FALL_SCALE;
			}
		}

		poseState.pitchVelocity += ((-forwardAcceleration * PITCH_ACCELERATION_SCALE) + ((float) accelerationY * -14.0F)) * motionMultiplier;
		poseState.rollVelocity += ((sidewaysAcceleration * ROLL_ACCELERATION_SCALE) - (yawDelta * TURN_SCALE)) * motionMultiplier;
		poseState.pitchVelocity += (fallPitchTarget - poseState.pitchAngle) * FALL_PITCH_STIFFNESS;

		if (wasOnGround && !onGround && velocity.y > 0.0) {
			poseState.pitchVelocity += JUMP_PITCH_IMPULSE * motionIntensity * modeMotionScale * (handLeftFirstPerson ? HAND_LEFT_MOTION_SCALE : 1.0F);
		} else if (!wasOnGround && onGround && lastVelocityY < -0.08) {
			poseState.pitchVelocity += Math.min((float) (-lastVelocityY * LANDING_PITCH_IMPULSE_SCALE * motionIntensity * modeMotionScale * (handLeftFirstPerson ? HAND_LEFT_FALL_SCALE : 1.0F)), MAX_LANDING_PITCH_IMPULSE);
			poseState.rollVelocity *= LANDING_ROLL_DAMPING;
		}

		wasOnGround = onGround;

		poseState.pitchVelocity += -poseState.pitchAngle * PITCH_STIFFNESS;
		poseState.rollVelocity += -poseState.rollAngle * ROLL_STIFFNESS;

		poseState.pitchVelocity *= pitchDamping;
		poseState.rollVelocity *= rollDamping;

		poseState.pitchAngle = Mth.clamp(poseState.pitchAngle + poseState.pitchVelocity, -MAX_PITCH, MAX_PITCH);
		poseState.rollAngle = Mth.clamp(poseState.rollAngle + poseState.rollVelocity, -MAX_ROLL, MAX_ROLL);
		poseState.yawLag = Mth.clamp((poseState.yawLag + yawDelta * 0.18F) * YAW_LAG_DAMPING, -MAX_YAW_LAG, MAX_YAW_LAG);
		poseState.bob = Mth.lerp(0.22F, poseState.bob, targetBob);
		if (handLeftFirstPerson) {
			poseState.pitchAngle *= HAND_LEFT_MOTION_SCALE;
			poseState.rollAngle *= HAND_LEFT_MOTION_SCALE;
			poseState.yawLag *= HAND_LEFT_YAW_SCALE;
			poseState.bob *= HAND_LEFT_BOB_SCALE;
		}
	}

	public LanternPoseState getPoseState() {
		return poseState;
	}

	private void decayToRest() {
		lastVelocityX = 0.0;
		lastVelocityY = 0.0;
		lastVelocityZ = 0.0;
		wasOnGround = false;
		poseState.pitchVelocity *= 0.75F;
		poseState.rollVelocity *= 0.75F;
		poseState.pitchAngle = Mth.lerp(0.2F, poseState.pitchAngle, 0.0F);
		poseState.rollAngle = Mth.lerp(0.2F, poseState.rollAngle, 0.0F);
		poseState.yawLag = Mth.lerp(0.2F, poseState.yawLag, 0.0F);
		poseState.bob = Mth.lerp(0.2F, poseState.bob, 0.0F);
	}
}
