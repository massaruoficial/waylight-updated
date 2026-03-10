package com.soradotwav.waylight.light;

import com.soradotwav.Waylight;
import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehaviorManager;
import org.joml.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.NonNull;

public final class LambDynamicLightsAdapter implements VirtualLightSource {
	private final WaylightLanternBehavior behavior = new WaylightLanternBehavior();
	private boolean registered;

	public void tick(Minecraft client) {
		if (!registered) {
			DynamicLightBehaviorManager behaviorManager = client.level != null
				? getBehaviorManager()
				: null;

			if (behaviorManager != null) {
				behaviorManager.add(behavior);
				registered = true;
			}
		}

		behavior.update(client);
	}

	private static DynamicLightBehaviorManager getBehaviorManager() {
		try {
			Class<?> dynamicLightsClass = Class.forName("dev.lambdaurora.lambdynlights.LambDynLights");
			Object instance = dynamicLightsClass.getMethod("get").invoke(null);
			return (DynamicLightBehaviorManager) dynamicLightsClass.getMethod("dynamicLightBehaviorManager").invoke(instance);
		} catch (ReflectiveOperationException exception) {
			Waylight.LOGGER.error("Failed to access LambDynamicLights behavior manager.", exception);
			return null;
		}
	}

	private static final class WaylightLanternBehavior implements DynamicLightBehavior {
		private final Vector3d position = new Vector3d();
		private final Vector3d previousPosition = new Vector3d(Double.NaN, Double.NaN, Double.NaN);
		private int luminance;
		private int previousLuminance = -1;
		private boolean removed;

		void update(Minecraft client) {
			LocalPlayer player = client.player;
			if (player == null || client.level == null || WaylightClient.LANTERN_CONTROLLER == null) {
				luminance = 0;
				removed = false;
				return;
			}

			VirtualLanternState state = WaylightClient.LANTERN_CONTROLLER.getState();
			luminance = state.lightActive() ? VirtualLightSource.LUMINANCE : 0;
			if (luminance <= 0) {
				return;
			}

			WaylightConfig config = WaylightClient.CONFIG_MANAGER.get();
			if (!WaylightClient.TRANSFORM_RESOLVER.resolveWorldEmission(player, state, WaylightClient.POSE_CONTROLLER.getPoseState(), config, position)) {
				luminance = 0;
			}
		}

		@Override
		public double lightAtPos(@NonNull BlockPos pos, double falloffRatio) {
			if (luminance <= 0) {
				return 0.0;
			}

			double dx = pos.getX() + 0.5 - position.x();
			double dy = pos.getY() + 0.5 - position.y();
			double dz = pos.getZ() + 0.5 - position.z();
			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			return Math.max(luminance - distance * falloffRatio, 0.0);
		}

		@Override
		public @NonNull BoundingBox getBoundingBox() {
			int x = (int) Math.floor(position.x());
			int y = (int) Math.floor(position.y());
			int z = (int) Math.floor(position.z());
			return new BoundingBox(x, y, z, x + 1, y + 1, z + 1);
		}

		@Override
		public boolean hasChanged() {
			boolean changed = !position.equals(previousPosition) || luminance != previousLuminance;
			if (changed) {
				previousPosition.set(position);
				previousLuminance = luminance;
			}
			return changed;
		}

		@Override
		public boolean isRemoved() {
			return removed;
		}
	}
}
