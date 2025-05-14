package io.github.sandydunlop.newsfeed;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;


public class NewsfeedClientModInitializer implements ClientModInitializer {
	private static final Identifier RENDER_LAYER = Identifier.of(NewsfeedModInitializer.MOD_ID);
	private static int tock = 0; //20 ticks = 1 second
	private static int interval = 10000;
	private static RssFeed rssFeed = new RssFeed();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Initialize drawContext before using it
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, RENDER_LAYER, NewsfeedClientModInitializer::render));
		rssFeed.setClient(MinecraftClient.getInstance());
	}


	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		if (tock++ > interval) {
			tock = 0;
			rssFeed.update();
		}
	}
}