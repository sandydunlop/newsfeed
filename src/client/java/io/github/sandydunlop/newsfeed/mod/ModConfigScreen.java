package io.github.sandydunlop.newsfeed.mod;

import io.github.sandydunlop.cupra.common.widgets.CWidget;
import io.github.sandydunlop.cupra.platform.minecraft.CupraMinecraftScreen;
import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;
import io.github.sandydunlop.newsfeed.app.Newsfeed;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;


public class ModConfigScreen extends CupraMinecraftScreen {
    private Screen parent;

    protected ModConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }


    @Override
    protected void init() {
        super.init();
        MinecraftServices platformServices = MinecraftServices.getInstance();
        platformServices.getApp().openScreen(Newsfeed.getConfigScreen());
    }


    @Override
    public void close() {
	    this.client.setScreen(this.parent);
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