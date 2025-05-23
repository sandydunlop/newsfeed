package io.github.sandydunlop.newsfeed;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;

import io.github.sandydunlop.cupra.UpdateChecker;


public class NewsfeedClientModInitializer implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(NewsfeedModInitializer.MOD_ID);
	private static final Identifier RENDER_LAYER = Identifier.of(NewsfeedModInitializer.MOD_ID);
	public static int tock = 0; //20 ticks = 1 second
	private static final int ONE_MINUTE = 1200; // 20 ticks * 60 seconds
	private static int interval = ONE_MINUTE;
	private static boolean doneStartupNotifications = false;
	private static RssFeed rssFeed;
	private static Path configFilePath = null;
	public static NewsfeedConfig config = new NewsfeedConfig();


	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Initialize drawContext before using it
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, RENDER_LAYER, NewsfeedClientModInitializer::render));
		NewsfeedClientModInitializer.loadConfig();
		rssFeed = new RssFeed();
		rssFeed.setClient(MinecraftClient.getInstance());

		KeyBinding keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"newsfeed.keybinds.open", // The translation key of the keybinding's name
			InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
			GLFW.GLFW_KEY_N, // The keycode of the key
			"newsfeed.keybinds.title" // The translation key of the keybinding's category.
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding1.wasPressed()) {
				Screen screen = getArticleScreen(null);
				MinecraftClient.getInstance().setScreen(screen);
			}
		});
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
			NewsfeedConfig.updateCheckEnabled = jsonObject.getBoolean("updateCheckEnabled");
		} catch(JSONException e){
			LOGGER.error("Problem loading config: {}", e.getMessage());
		} catch(IOException e){
			LOGGER.error("Unable to read config file: {}", e.getMessage());
		}
	}


	public static Screen getArticleScreen(Screen parent) {
		return new NewsfeedArticleScreen(net.minecraft.text.Text.of("article"), parent, rssFeed);
	}


	public static Screen getConfigScreen(Screen parent) {
		return new NewsfeedConfigScreen(net.minecraft.text.Text.translatable("newsfeed.config.title"), parent, configFilePath);
	}


	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		if (tock++ > interval) {
			tock = 0;
			rssFeed.update();
		}
		if (!doneStartupNotifications && tock > 100) {
			if (NewsfeedConfig.updateCheckEnabled) {
				UpdateChecker updateChecker = new UpdateChecker(NewsfeedModInitializer.MOD_ID);
				if (updateChecker.isUpdateAvailable()) {
					LOGGER.info("Update available for " + NewsfeedModInitializer.MOD_ID);
					String msg = String.format("Update available for %s: %s", NewsfeedModInitializer.MOD_ID, updateChecker.getLatestVersion());
					LOGGER.info(msg);
					if (MinecraftClient.getInstance().player != null)
						MinecraftClient.getInstance().player.sendMessage(Text.of(msg), true);
				}
			}
			doneStartupNotifications = true;
		}
	}


	public static void updateNow() {
		tock = interval;
	}
}