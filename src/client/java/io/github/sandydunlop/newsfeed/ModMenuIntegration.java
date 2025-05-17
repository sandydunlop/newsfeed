package io.github.sandydunlop.newsfeed;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return NewsfeedClientModInitializer.getConfigScreen(parent);
        };
    }
}
