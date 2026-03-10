package com.soradotwav;

import net.fabricmc.api.ClientModInitializer;
import com.soradotwav.waylight.config.WaylightConfigManager;

public class WaylightClient implements ClientModInitializer {
	public static final WaylightConfigManager CONFIG_MANAGER = new WaylightConfigManager();

	@Override
	public void onInitializeClient() {
		CONFIG_MANAGER.load();
		Waylight.LOGGER.info("Initializing {} client", Waylight.MOD_ID);
	}
}
