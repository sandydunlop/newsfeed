package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

import io.github.sandydunlop.cupra.gui.palette.ColorPalette;

public class CListBoxEntry extends PressableWidget {
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
		int backgroundColor = CWidget.color.REGULAR_BACKGROUND;
		int textColor = CWidget.color.REGULAR_TEXT;
		if (isHovered(mouseX, mouseY)){
			backgroundColor = CWidget.color.HOVERED_BACKGROUND;
		}
		if (isSelected) {
			backgroundColor = CWidget.color.SELECTED_BACKGROUND;
			textColor = CWidget.color.SELECTED_TEXT;
		}
        context.fill(getX(), renderY, getX() + getWidth(), renderY + getHeight(), backgroundColor);
		if (isHovered(mouseX, mouseY)){
			int borderColor = CWidget.color.HOVERED_BORDER;
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
	protected void appendClickableNarrations(NarrationMessageBuilder var1) {}


	public String getTitle() {
		return title;
	}


    public void ifPresent(Object object) {
		return;
    }
}