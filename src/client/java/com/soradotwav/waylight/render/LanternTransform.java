package com.soradotwav.waylight.render;

public record LanternTransform(
	Attachment attachment,
	float translateX,
	float translateY,
	float translateZ,
	float rotateX,
	float rotateY,
	float rotateZ,
	float emissionX,
	float emissionY,
	float emissionZ
) {
	public enum Attachment {
		BODY,
		LEFT_ARM
	}
}
