package io.github.sandydunlop.newsfeed.mod;

import java.nio.file.Path;

import io.github.sandydunlop.cupra.platform.minecraft.CupraMinecraftScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;


public class ModConfigScreen extends CupraMinecraftScreen {

    protected ModConfigScreen(Text title, Screen parent, Path configFile) {
        super(title);
    }

}