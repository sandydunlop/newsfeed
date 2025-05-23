package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CButton extends CWidget{
    private ButtonWidget widget = null;
    CPressAction onPress;


    public CButton (CGUIScreen screen, Text message, CPressAction onPress){
        this.onPress = onPress;
        widget = ButtonWidget.builder(message, (btn)->{
            this.onPress.onPress(this);
        }).build();
		screen.addDrawableChild(widget);
    }


    public void setEnabled(boolean flag){
        widget.active = flag;
    }


    public interface CPressAction {
        void onPress(CButton button);
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

}

