package io.github.sandydunlop.newsfeed.mod;

import java.nio.file.Path;

import io.github.sandydunlop.cupra.platform.minecraft.CupraMinecraftScreen;
import io.github.sandydunlop.cupra.platform.minecraft.MinecraftServices;
import io.github.sandydunlop.newsfeed.app.Newsfeed;
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
        //layoutAppScreen(); // TODO
    }

    @Override
    public void close() {
	    this.client.setScreen(this.parent);
	}
}