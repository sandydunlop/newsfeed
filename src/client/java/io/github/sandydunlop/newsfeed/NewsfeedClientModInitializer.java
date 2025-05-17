package io.github.sandydunlop.newsfeed;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;


public class NewsfeedClientModInitializer implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(NewsfeedModInitializer.MOD_ID);
	private static final Identifier RENDER_LAYER = Identifier.of(NewsfeedModInitializer.MOD_ID);
	private static int tock = 0; //20 ticks = 1 second
	private static int interval = 10000;
	private static RssFeed rssFeed = new RssFeed();
	private static Path configFilePath = null;
	public static NewsfeedConfig config = new NewsfeedConfig();


	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Initialize drawContext before using it
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, RENDER_LAYER, NewsfeedClientModInitializer::render));
		rssFeed.setClient(MinecraftClient.getInstance());
		NewsfeedClientModInitializer.loadConfig();
	}


	public static void loadConfig() {
		if (configFilePath == null) {
			configFilePath = FabricLoader.getInstance().getConfigDir().resolve(NewsfeedModInitializer.MOD_ID + ".json");
		}
		try {
			String json = IOUtils.toString(configFilePath.toUri(), Charset.forName("UTF-8"));
			JSONObject jsonObject = new JSONObject(json);
			NewsfeedConfig.feedName = jsonObject.getString("feedName");
			NewsfeedConfig.feedUrl = jsonObject.getString("feedUrl");
			NewsfeedConfig.feedEnabled = jsonObject.getBoolean("feedEnabled");
		} catch(JSONException e){
			LOGGER.error("Problem loading config: {}", e.getMessage());
		} catch(IOException e){
			LOGGER.error("Unable to read config file: {}", e.getMessage());
		}
	}


	public static Screen getConfigScreen(Screen parent) {
		return new NewsfeedConfigScreen(net.minecraft.text.Text.translatable("newsfeed.config.title"), parent, configFilePath);
	}


	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		if (tock++ > interval) {
			tock = 0;
			rssFeed.update();
		}
	}
}