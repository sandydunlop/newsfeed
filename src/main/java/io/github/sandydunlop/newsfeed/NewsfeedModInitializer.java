package io.github.sandydunlop.newsfeed;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.midnightdust.lib.config.MidnightConfig;


public class NewsfeedModInitializer implements ModInitializer {
	public static final String MOD_ID = "newsfeed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		MidnightConfig.init("newsfeed", NewsfeedConfig.class);
	}
}