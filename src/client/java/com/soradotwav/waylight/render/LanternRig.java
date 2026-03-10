package com.soradotwav.waylight.render;

public record LanternRig(LanternTransform transform) {
	public LanternTransform.Attachment attachment() {
		return transform.attachment();
	}

	public float lightCoreX() {
		return transform.emissionX();
	}

	public float lightCoreY() {
		return transform.emissionY();
	}

	public float lightCoreZ() {
		return transform.emissionZ();
	}
}
