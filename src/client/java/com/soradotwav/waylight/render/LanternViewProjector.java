package com.soradotwav.waylight.render;

public final class LanternViewProjector {
	private static final float FIRST_PERSON_BASE_X = -0.84F;
	private static final float FIRST_PERSON_BASE_Y = 0.78F;
	private static final float FIRST_PERSON_BASE_Z = -0.92F;
	private static final float FIRST_PERSON_BASE_ROT_X = 8.0F;
	private static final float FIRST_PERSON_BASE_ROT_Y = 16.0F;
	private static final float FIRST_PERSON_BASE_ROT_Z = -8.0F;
	private static final float FIRST_PERSON_BASE_SCALE = 1.08F;

	private static final float HAND_LEFT_BASE_TRANSLATE_X = 0.05F;
	private static final float HAND_LEFT_BASE_TRANSLATE_Y = 0.60F;
	private static final float HAND_LEFT_BASE_TRANSLATE_Z = 0.10F;
	private static final float HAND_LEFT_BASE_ROT_X = -50.0F;
	private static final float HAND_LEFT_BASE_ROT_Y = -10.0F;
	private static final float HAND_LEFT_BASE_ROT_Z = 0.0F;

	public FirstPersonProjection projectHandLeft(LanternRig rig) {
		LanternTransform transform = rig.transform();
		float translateXDelta = transform.translateX() - HAND_LEFT_BASE_TRANSLATE_X;
		float translateYDelta = transform.translateY() - HAND_LEFT_BASE_TRANSLATE_Y;
		float translateZDelta = transform.translateZ() - HAND_LEFT_BASE_TRANSLATE_Z;
		float rotateXDelta = transform.rotateX() - HAND_LEFT_BASE_ROT_X;
		float rotateYDelta = transform.rotateY() - HAND_LEFT_BASE_ROT_Y;
		float rotateZDelta = transform.rotateZ() - HAND_LEFT_BASE_ROT_Z;

		return new FirstPersonProjection(
			FIRST_PERSON_BASE_X + translateXDelta,
			FIRST_PERSON_BASE_Y + translateYDelta,
			FIRST_PERSON_BASE_Z + translateZDelta,
			FIRST_PERSON_BASE_ROT_X + rotateXDelta,
			FIRST_PERSON_BASE_ROT_Y + rotateYDelta,
			FIRST_PERSON_BASE_ROT_Z + rotateZDelta,
			FIRST_PERSON_BASE_SCALE
		);
	}

	public record FirstPersonProjection(
		float translateX,
		float translateY,
		float translateZ,
		float rotateX,
		float rotateY,
		float rotateZ,
		float scale
	) {
	}
}
