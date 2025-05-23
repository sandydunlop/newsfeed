package io.github.sandydunlop.cupra.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CGUIScreen extends Screen{
    private final Screen parent;
	private List<CWidget> widgets;

	final int WIDGET_HEIGHT = 20;
	final int MEDIUM_VERTICAL_GAP = 10;
	int y = 60;


    public CGUIScreen(Text title, Screen parent) {
		super(title);
		this.parent = parent;
	}

    public CGUIScreen(Text title) {
		super(title);
        parent = null;
	}


    @Override
	protected void init() {
		widgets = new ArrayList<>();
    }

	@Override
	public void close() {
        this.client.setScreen(parent);
	}

    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		T r = super.addDrawableChild(drawableElement);
		return r;
	}

	public void setTextRenderer(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}


	public void add(CWidget widget){
		this.widgets.add(widget);
	}



	public void layout(){
		final int screenWidth = this.width;
		final int screenHeight = this.height;
		final int marginLeft = (int)(screenWidth * 0.1);

		int topSectionHeight = 40;
		int bottomSectionHeight = 40;

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

		int y = topSectionHeight;

		for (CWidget widget : widgets) {
			widget.setWidth((int)(this.width * 0.8));
			widget.setX(marginLeft);
			widget.setY(y);
			y += widget.getHeight();
		}

    }

}
