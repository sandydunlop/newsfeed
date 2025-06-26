package io.github.sandydunlop.newsfeed.mod;

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
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;
import io.github.sandydunlop.newsfeed.app.Newsfeed;
import io.github.sandydunlop.newsfeed.app.NewsfeedConfig;


public class NewsfeedClientModInitializer implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
	private static final Identifier RENDER_LAYER = Identifier.of("newsfeed");
	private static final int ONE_MINUTE = 1200; // 20 ticks * 60 seconds
	private static final int INTERVAL = ONE_MINUTE;
	private static int tock = 0; //20 ticks = 1 second
	private static boolean doneStartupNotifications = false;
	public static final KeyBinding newsfeedKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"Open Newsfeed", // The translation key of the keybinding's name
		InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
		GLFW.GLFW_KEY_N, // The keycode of the key
		"Newsfeed" // The translation key of the keybinding's category.
	));


	@Override
	public void onInitializeClient() {
		MinecraftServices platformServices = MinecraftServices.getInstance();
		ModUtils.toAssist(this);
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Initialize drawContext before using it
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, RENDER_LAYER, NewsfeedClientModInitializer::render));

		NewsfeedConfig.loadConfig(FabricLoader.getInstance().getConfigDir().resolve(NewsfeedModInitializer.MOD_ID + ".json"));
		Newsfeed app = new Newsfeed();
		platformServices.setApp(app);
		Newsfeed.getBackgroundThread();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (newsfeedKeyBind.wasPressed()) {
				Screen screen = getArticleScreen(null);
				MinecraftClient.getInstance().setScreen(screen);
			}
		});
	}


	public static Screen getArticleScreen(Screen parent) {
		return new ModMainScreen(parent);
	}


	public static Screen getConfigScreen(Screen parent) {
		return new ModConfigScreen(net.minecraft.text.Text.translatable("newsfeed.config.title"), parent);
	}


	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		if (tock++ > INTERVAL) {
			tock = 0;
		}
		// //TODO Move this out of render method into new thread
		if (!doneStartupNotifications && tock > 100) {
			if (NewsfeedConfig.updateCheckEnabled) {
				if (ModUtils.isUpdateAvailable()) {
					LOGGER.info("Update available for " + NewsfeedModInitializer.MOD_ID);
					String msg = String.format("Update available for %s: %s", NewsfeedModInitializer.MOD_ID, ModUtils.getLatestVersion());
					LOGGER.info(msg);
					if (MinecraftClient.getInstance().player != null) {
						MinecraftServices.getInstance().showNotification(msg);
					}
				}
			}
			doneStartupNotifications = true;
		}
		MinecraftServices.getTicker().render(context);
	}


	public static void updateNow() {
		tock = INTERVAL;
	}
}