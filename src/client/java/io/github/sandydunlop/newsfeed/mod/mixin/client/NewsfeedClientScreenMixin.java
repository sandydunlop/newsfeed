package io.github.sandydunlop.newsfeed.mod.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.sandydunlop.newsfeed.mod.ModMainScreen;
import io.github.sandydunlop.newsfeed.mod.NewsfeedClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;


@Mixin(Screen.class)
public abstract class NewsfeedClientScreenMixin {
	@Shadow
    public abstract void close();

	@Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
	public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
		if (keyCode == KeyBindingHelper.getBoundKeyOf(NewsfeedClientModInitializer.newsfeedKeyBind).getCode()) {
			if ((Object)this instanceof ModMainScreen){
				NewsfeedClientModInitializer.newsfeedKeyBind.setPressed(false);
				info.setReturnValue(true);
				this.close();
			}
		}
	}
}