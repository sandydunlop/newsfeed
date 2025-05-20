package io.github.sandydunlop.newsfeed;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Clipboard;


public class ClipboardHelper 
{
    public static String getClipboardText() {
        MinecraftClient client = MinecraftClient.getInstance();
        long handle = client.getWindow().getHandle();
        Clipboard clipboard = new net.minecraft.client.util.Clipboard();
        return clipboard.getClipboard(handle, ClipboardHelper::onFullscreenError);
    }

	private static void onFullscreenError(int i, long l) {
	}
}