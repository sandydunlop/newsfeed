package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;


public class CLabel extends CWidget {
    private TextWidget widget = null;


    public CLabel(CGUIScreen screen, Text text) {
        widget = new TextWidget(text, screen.getTextRenderer());
        widget.alignLeft();
		screen.addDrawableChild(widget);
    }


    public void setText(Text text) {
        widget.setMessage(text);
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
    }
    

    public void setHeight(int height) {
        widget.setHeight(height);
    }


    public int getHeight() {
        return widget.getHeight();
    }
}
