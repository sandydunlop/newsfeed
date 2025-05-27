package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.text.Text;


public class CListBoxEntry extends PressableWidget {
	private boolean initialized = false;
	private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	private final String title;
	int renderY;
	Object value;


	public CListBoxEntry(String title, Object value) {
		super(0, 0, 0, 0, Text.literal(""));
		this.title = title;
		this.value = value;
	}


	@Override
	public void onPress() {};


	public Object getValue() {
		return value;
	}


	public void renderF(int scroll, DrawContext context, int mouseX, int mouseY, float delta, boolean isSelected) {
		renderY = getY() - scroll;
		int backgroundColor = 0x88303030;
		int textColor = 0xFF808080;
		if (isHovered(mouseX, mouseY)){
			backgroundColor = 0xFF3B4017;
		}
		if (isSelected) {
			backgroundColor = 0xFFB2C248;
			textColor = 0xFF000000;
		}
        context.fill(getX(), renderY, getX() + getWidth(), renderY + getHeight(), backgroundColor);
		if (isHovered(mouseX, mouseY)){
			int borderColor = 0xFF626B27;
			context.drawHorizontalLine(getX(), getX() + getWidth() - 1, renderY, borderColor);
			context.drawHorizontalLine(getX(), getX() + getWidth() - 1, renderY + getHeight() -1, borderColor);
			context.drawVerticalLine(getX(), renderY, renderY + getHeight() - 1, borderColor);
			context.drawVerticalLine(getX() + getWidth() - 1, renderY + getHeight() - 1, renderY, borderColor);
		}
		context.drawText(textRenderer, title, getX() + 2, renderY + 2, textColor, false);
	}


	@Override
	public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
		this.drawScrollableText(context, textRenderer, 2, color);
	}

			
	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= renderY && mouseY < renderY + getHeight();
	}


	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!initialized || !this.active || !this.visible) return false;
		if (KeyCodes.isToggle(keyCode)) {
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			this.onPress();
			return true;
		}
		return false;
	}


	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder var1) {}


	public String getTitle() {
		return title;
	}


    public void ifPresent(Object object) {
		return;
    }
}