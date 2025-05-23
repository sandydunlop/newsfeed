package io.github.sandydunlop.cupra.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CGUIScreen extends Screen{
    private final Screen parent;
	private CContainer header;
	private CContainer body;
	private CContainer footer;


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
		header = new CContainer(this);
		body = new CContainer(this);
		footer = new CContainer(this);
    }


	private void resizeContainers(){
		float marginX = 0.1f;
		int headerHeight = 40;
		int footerHeight = 40;
		header.setX((int)(marginX * this.width));
		header.setY(0);
		header.setWidth((int)(this.width * (1-(marginX * 2))));
		header.setHeight(headerHeight);
		body.setX((int)(marginX * this.width));
		body.setY(headerHeight);
		body.setWidth((int)(this.width * (1-(marginX * 2))));
		body.setHeight(this.height - (headerHeight + footerHeight));
		footer.setX((int)(marginX * this.width));
		footer.setY(this.height - footerHeight);
		footer.setWidth((int)(this.width * (1-(marginX * 2))));
		footer.setHeight(footerHeight);
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


	public void addToHeader(CWidget widget){
		header.add(widget);
	}


	public void addToBody(CWidget widget){
		body.add(widget);
	}


	public void addToFooter(CWidget widget){
		footer.add(widget);
	}


	public void layout(){
		resizeContainers();
		body.layout();
	}
}
