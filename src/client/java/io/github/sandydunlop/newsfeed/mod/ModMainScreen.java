package io.github.sandydunlop.newsfeed.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import io.github.sandydunlop.cupra.common.widgets.CWidget;
import io.github.sandydunlop.cupra.platform.minecraft.CupraMinecraftScreen;
import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;


public class ModMainScreen extends CupraMinecraftScreen {
	Screen parent = null;


    public ModMainScreen(Screen parent) {
		super(Text.of(""));
		this.parent = parent;
	}


    @Override
	protected void init() {
		super.init();
		MinecraftServices platformServices = MinecraftServices.getInstance();
		MinecraftServices.getTicker().setHidden(true);
		platformServices.getApp().run();
		layoutAppScreen();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (NewsfeedClientModInitializer.newsfeedKeyBind.wasPressed()) {
				MinecraftServices.getTicker().setHidden(false);
				MinecraftClient.getInstance().setScreen(null);
			}
		});
    }


    @Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (client.player != null){
			this.applyBlur();
			this.renderInGameBackground(context);
			context.fill(0, 0, this.width, this.height, 0x88FFFFFF & CWidget.getPalette().REGULAR_BACKGROUND);
		}
		super.render(context, mouseX, mouseY, delta);
    }
}
