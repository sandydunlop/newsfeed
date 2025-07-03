package io.github.sandydunlop.newsfeed.mod;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;

import io.github.sandydunlop.cupra.common.logging.LogManager;
import io.github.sandydunlop.cupra.common.logging.Logger;
import io.github.sandydunlop.cupra.common.widgets.CWidget;
import io.github.sandydunlop.cupra.platform.minecraft.CupraMinecraftScreen;
import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;


public class ModMainScreen extends CupraMinecraftScreen {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
	Screen parent = null;


    public ModMainScreen(Screen parent) {
		super(Text.of(""));
		this.parent = parent;
		MinecraftServices.getTicker().setHidden(true);
		MinecraftServices.getInstance().getApp().run();
	}


    @Override
	protected void init() {
		super.init();
		layoutAppScreen();
    }


    @Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (client.player != null){
			this.applyBlur(context);
			this.renderInGameBackground(context);
			context.fill(0, 0, this.width, this.height, 0x88FFFFFF & CWidget.getPalette().REGULAR_BACKGROUND);
		}
		super.render(context, mouseX, mouseY, delta);
    }
}
