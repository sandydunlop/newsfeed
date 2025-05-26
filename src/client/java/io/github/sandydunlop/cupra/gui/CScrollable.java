package io.github.sandydunlop.cupra.gui;

import io.github.sandydunlop.newsfeed.NewsfeedModInitializer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class CScrollable extends CContainer {
	private static final Identifier SCROLLER_TEXTURE = Identifier.of(NewsfeedModInitializer.MOD_ID,"widget/scroller");
    protected final int innerHeight;
    private double scrollAmount;
    private boolean scrolling;
    private CScrollableContents content = null;


    public CScrollable(CGUIScreen parent, int width, int widgetHeight, int x, int y, int innerHeight) {
    	super(parent, x, y, width, widgetHeight);
        this.innerHeight = innerHeight;
        parent.addDrawableChild(this);
    }
    

    public void setContent(CScrollableContents widget) {
        this.content = widget;
        this.contentHeight = widget.getHeight();
        this.setHeight(this.contentHeight);
    }


    protected int getMaxPosition() {
        return this.innerHeight;
    }


    public int getRight() {
        return this.getX() + this.getWidth();
    }


    public int getBottom() {
        return this.getY() + this.getHeight();
    }


    public void layout() {
        content.setPos(getX(), getY());
        content.setWidth(getWidth() - 6);
    }
    

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int j;
        int i;
        if ((i = this.getMaxScroll()) > 0) {
        	j = this.getScrollbarPositionX();
            int k = (int)((float)(this.getHeight() * this.getHeight()) / (float)this.getMaxPosition());
            k = MathHelper.clamp((int)k, (int)32, (int)(this.getHeight() - 8));
            int l = (int)this.getScrollAmount() * (this.getHeight() - k) / i + this.getY();
            if (l < this.getY()) l = this.getY();
            context.fill(j, this.getY(), j + 6, this.getBottom(), -16777216);
            context.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_TEXTURE, j, l, 6, k);
        }
    }


    public void enableScissor(DrawContext context) {
    	context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    }


    public double getScrollAmount() {
        content.setScrollAmount(this.scrollAmount);
    	return this.scrollAmount;
    }


    public void setScrollAmount(double amount) {
    	this.scrollAmount = MathHelper.clamp((double)amount, (double)0.0, (double)this.getMaxScroll());
    }


    public int getMaxScroll() {
    	return Math.max(0, this.getMaxPosition() - (this.getHeight() - 4));
    }


    protected void updateScrollingState(double mouseX, double mouseY, int button) {
    	this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
    }


    protected int getScrollbarPositionX() {
    	return this.getX() + this.getWidth() - 6;
    }


    protected boolean isSelectButton(int button) {
    	return button == 0;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //System.out.println("CScrollable mouseClicked: " + mouseX + ", " + mouseY + ", button: " + button);
    	if (button != 0) return false;
    	this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) return false;
        return this.scrolling;
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        this.scrolling = false;
        return false;
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0 || !this.scrolling) return false;
        if (mouseY < (double)this.getY()) {
        	this.setScrollAmount(0.0);
        } else if (mouseY > (double)this.getBottom()) {
        	this.setScrollAmount(this.getMaxScroll());
        } else {
        	double d = Math.max(1, this.getMaxScroll());
            int i = this.getHeight();
            int j = MathHelper.clamp((int)((int)((float)(i * i) / (float)this.getMaxPosition())), (int)32, (int)(i - 8));
            double e = Math.max(1.0, d / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + deltaY * e);
        }
        return true;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.setScrollAmount(this.getScrollAmount() - verticalAmount * 0.25 * (double)this.innerHeight / 2.0);
        return true;
    }


    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.getY() && mouseY <= (double)this.getBottom() && mouseX >= (double)this.getX() && mouseX <= (double)this.getRight();
    }


    //TODO: Use this
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
    	
        int i = this.getX() + (this.getWidth() - entryWidth) / 2;
        int j = this.getX() + (this.getWidth() + entryWidth) / 2;
        
        context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
        context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }

	// @Override
	// public List<? extends Element> children() {
		
	// 	return null;
	// }

	// @Override
	// protected void appendClickableNarrations(NarrationMessageBuilder var1) {}
}