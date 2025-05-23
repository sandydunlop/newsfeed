package io.github.sandydunlop.cupra.gui;

import java.util.List;

import io.github.sandydunlop.newsfeed.MultiLineTextWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class CMultiLineTextBox extends CWidget {
    private MultiLineTextWidget widget = null;
    private Text text = null;
	List<OrderedText> wrappedDescription = null;
    private Screen screen = null;


    public CMultiLineTextBox(CGUIScreen screen, Text text) {
        this.screen = screen;
		int descriptionLabelWidth = (int)(screen.width * 0.8);
		//wrappedDescription = screen.getTextRenderer().wrapLines(Text.of(text), descriptionLabelWidth);
        widget = new MultiLineTextWidget(screen.getTextRenderer());
        widget.setWidth(descriptionLabelWidth);
        setText(text);
        //MultiLineTextWidget(wrappedDescription, textRenderer, marginLeft, y, descriptionLabelWidth, descriptionLabelHeight);
        //screen.add(this);
		screen.addDrawableChild(widget);
    }

    public void setText(Text text) {
        this.text = text;
        wrapLines();
    }

    public void wrapLines() {
		int descriptionPadding = 2; //TODO
		wrappedDescription = screen.getTextRenderer().wrapLines(Text.of(text), widget.getWidth() - (descriptionPadding*2));
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
