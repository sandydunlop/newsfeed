package io.github.sandydunlop.cupra.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CContainer extends CWidget {
    private final CGUIScreen parent;
	private List<CWidget> widgets;
    private TextRenderer textRenderer;

	final int WIDGET_HEIGHT = 20;
	final int MEDIUM_VERTICAL_GAP = 10;


    public CContainer(CGUIScreen parent) {
		this.parent = parent;
        init();
	}

    public CContainer(CGUIScreen parent, int x, int y, int width, int height) {
		this.parent = parent;
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
        init();
	}


    //@Override
	protected void init() {
		widgets = new ArrayList<>();
    }

	// @Override
	// public void close() {
    //     this.client.setScreen(parent);
	// }

    // protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
	// 	T r = super.addDrawableChild(drawableElement);
	// 	return r;
	// }

	public void setTextRenderer(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}


	public void add(CWidget widget){
		this.widgets.add(widget);
	}



	public void layout(){
		final int screenWidth = getWidth();
		final int screenHeight = getHeight();
		final int marginLeft = 0; //(int)(screenWidth * 0.1);

		int topSectionHeight = 0;
		int bottomSectionHeight = 0;

		int nonexHeight = 0;
		List<CWidget> expandables = new ArrayList<>();
		for (CWidget widget : widgets) {
			if (widget instanceof CMultiLineTextBox){
				expandables.add(widget);
			}else{
				widget.setHeight(WIDGET_HEIGHT);
				nonexHeight += WIDGET_HEIGHT;
			}
		}
		int expandableScreenHeight = screenHeight - topSectionHeight - bottomSectionHeight - nonexHeight;
		for(CWidget widget : expandables) {
			widget.setHeight(expandableScreenHeight / expandables.size());
		}

		int layoutY = topSectionHeight;

		for (CWidget widget : widgets) {
			widget.setWidth(getWidth());
			widget.setX(marginLeft + getX());
			widget.setY(layoutY + getY());
			layoutY += widget.getHeight();
		}

    }

}
