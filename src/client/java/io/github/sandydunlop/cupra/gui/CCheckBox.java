package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;


public class CCheckBox extends CWidget {
    private CheckboxWidget widget = null;


    public CCheckBox(CGUIScreen screen, Text text) {
		widget = CheckboxWidget.builder(text, screen.getTextRenderer())
			.checked(false)
			.build();
		screen.addDrawableChild(widget);
    }


    public CCheckBox(CGUIScreen screen, Text text, boolean checked) {
		widget = CheckboxWidget.builder(text, screen.getTextRenderer())
			.checked(checked)
			.build();
		screen.addDrawableChild(widget);
    }


    public void setText(Text text) {
        widget.setMessage(text);
    }

    
    public boolean isChecked() {
        return widget.isChecked();
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
