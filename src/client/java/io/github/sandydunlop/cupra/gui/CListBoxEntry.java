package io.github.sandydunlop.cupra.gui;

import java.awt.Color;

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


	public CListBoxEntry(String title) {
		super(0, 0, 0, 0, Text.literal(""));
		this.title = title;
	}


	@Override
	public void onPress() {};


	public void renderF(int scroll, DrawContext context, int mouseX, int mouseY, float delta, boolean isSelected) {
		renderY = getY() - scroll;
		int backgroundColor = isHovered(mouseX, mouseY) ? 0xFF0000FF : 0xFF000080; // Blue when hovered, dark blue otherwise
		if (isSelected) {
			backgroundColor = 0xFF008000; // Green when selected
		}
        context.fill(getX(), renderY, getX() + getWidth(), renderY + getHeight(), backgroundColor);
		context.drawTextWithShadow(textRenderer, title, getX() + 1, renderY + 1, Color.WHITE.getRGB());
	}


	@Override
	public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
		this.drawScrollableText(context, textRenderer, 2, color);
	}


	@Override
	public void onClick(double mouseX, double mouseY) {
		System.out.println("ItemCard clicked: " + title);
	}


	//TODO
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
		System.out.println("ItemCard mouseClicked: " + title + " at " + mouseX + ", " + mouseY);
        // if (!this.isSelectButton(button)) {
        //     return false;
        // }
        // this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        //E lv = this.getEntryAtPosition(mouseX, mouseY);
		return true;
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
}