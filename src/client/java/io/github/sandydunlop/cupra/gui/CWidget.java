package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import io.github.sandydunlop.cupra.gui.palette.ColorPalette;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;


public class CWidget extends ClickableWidget{
	protected static ColorPalette color = ColorPalette.Copper;
    private int x;
    private int y;
    private int width;
    private int height;

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // Implement narration logic or leave empty if not needed
    }

    @Override
    protected void renderWidget(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        // Implement rendering logic or leave empty if not needed
    }


    public CWidget() {
        super(0,0,0,0, Text.of(""));
    }


    public void setX(int x) {
        this.x = x;
    }


    public void setY(int y) {
        this.y = y;
    }


    public void setWidth(int width) {
        this.width = width;
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public int getHeight() {
        return height;
    }

    
    public int getWidth() {
        return width;
    }

    public void layout(){
        // This is overridden in subclasses
    }
}
