package io.github.sandydunlop.newsfeed.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
// import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
// import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;
import io.github.sandydunlop.cupra.platform.minecraft.Ticker;
import io.github.sandydunlop.newsfeed.app.Newsfeed;
import io.github.sandydunlop.newsfeed.app.NewsfeedConfig;


public class NewsfeedClientModInitializer implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
	private static final Identifier RENDER_LAYER = Identifier.of("newsfeed");
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

		NewsfeedConfig.loadConfig(FabricLoader.getInstance().getConfigDir().resolve(NewsfeedModInitializer.MOD_ID + ".json"));
		Newsfeed app = new Newsfeed();
		platformServices.setApp(app);
		Newsfeed.getBackgroundThread();

		Ticker ticker = MinecraftServices.getTicker();
		HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, RENDER_LAYER, ticker);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (newsfeedKeyBind.wasPressed()) {
				Screen screen = getArticleScreen(null);
				MinecraftClient.getInstance().setScreen(screen);
			}
		});

		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(10000);
				checkForUpdate();
			} catch (InterruptedException e) {
				LOGGER.error("Unable to check for new version");
				LOGGER.error(e.getMessage());
				Thread.currentThread().interrupt();
			}
		});
		thread.start();
	}


	public static Screen getArticleScreen(Screen parent) {
		return new ModMainScreen(parent);
	}


	public static Screen getConfigScreen(Screen parent) {
		return new ModConfigScreen(net.minecraft.text.Text.translatable("newsfeed.config.title"), parent);
	}


	private void checkForUpdate() {
		if (ModUtils.isUpdateAvailable()) {
			LOGGER.info("Update available for " + NewsfeedModInitializer.MOD_ID);
			String msg = String.format("Update available for %s: %s", NewsfeedModInitializer.MOD_ID, ModUtils.getLatestVersion());
			LOGGER.info(msg);
			if (MinecraftClient.getInstance().player != null) {
				MinecraftServices.getInstance().showNotification(msg);
			}
		}
	}
}