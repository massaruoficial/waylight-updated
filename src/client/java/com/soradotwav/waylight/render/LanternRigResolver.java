package com.soradotwav.waylight.render;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.VirtualLanternState;

public final class LanternRigResolver {
	private final LanternTransformResolver transformResolver = new LanternTransformResolver();

	public LanternRig resolveThirdPerson(VirtualLanternState lanternState, LanternPoseState poseState, WaylightConfig config) {
		return new LanternRig(transformResolver.resolveThirdPerson(lanternState, poseState, config));
	}

	public LanternTransformResolver transformResolver() {
		return transformResolver;
	}
}
