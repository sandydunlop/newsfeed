package io.github.sandydunlop.newsfeed.mod;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import io.github.sandydunlop.cupra.platform.minecraft.CupraMinecraftScreen;
import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;
import io.github.sandydunlop.newsfeed.app.Newsfeed;


public class ModMainScreen extends CupraMinecraftScreen {
    private static final String MOD_ID = "newsfeed";
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	Screen parent = null;
    private int homeButtonTop = 0;
    private int homeButtonLeft = 0;


    public ModMainScreen(Screen parent) {
		super(Text.of(""));
		this.parent = parent;
	}


    @Override
	protected void init() {
		super.init();
		MinecraftServices platformServices = MinecraftServices.getInstance();
		platformServices.getApp().display();
		layoutAppScreen();
    }


    @Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (client.player != null){
			this.applyBlur();
			this.renderInGameBackground(context);
			context.fill(10, 10, this.width-10, this.height - 10, 0x88000000);
		}
		super.render(context, mouseX, mouseY, delta);
    }
}
