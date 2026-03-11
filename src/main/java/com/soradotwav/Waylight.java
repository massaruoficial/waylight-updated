package com.soradotwav;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waylight implements ModInitializer {
    public static final String MOD_ID = "waylight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.debug("Initializing {}", MOD_ID);
    }
}
