package io.github.sandydunlop.cupra.gui;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import io.github.sandydunlop.newsfeed.MultiLineTextWidget;


public class CMultiLineTextBox extends CWidget {
    private final int CONTENT_PADDING = 2;
    private MultiLineTextWidget widget = null;
    private Text text = null;
	List<OrderedText> wrappedDescription = null;
    private Screen screen = null;


    public CMultiLineTextBox(CGUIScreen screen, Text text) {
        this.screen = screen;
		int descriptionLabelWidth = (int)(screen.width * 0.8);
        widget = new MultiLineTextWidget(screen.getTextRenderer());
        widget.setWidth(descriptionLabelWidth);
        setText(text);
		screen.addDrawableChild(widget);
    }


    public void setText(Text text) {
        this.text = text;
        wrapLines();
    }


    public void wrapLines() {
		wrappedDescription = screen.getTextRenderer().wrapLines(Text.of(text), widget.getWidth() - (CONTENT_PADDING*2));
        widget.setLines(wrappedDescription);
    }


    public void setTooltip(Tooltip tooltip){
        widget.setTooltip(tooltip);
    }


    public void setX(int x) {
        widget.setX(x);
    }


    public void setY(int y) {
        widget.setY(y);
    }


    public void setWidth(int width) {
        widget.setWidth(width);
        wrapLines();
    }
    

    public void setHeight(int height) {
        widget.setHeight(height);
    }


    public int getHeight() {
        return widget.getHeight();
    }
}
