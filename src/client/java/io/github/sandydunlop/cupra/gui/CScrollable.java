package io.github.sandydunlop.cupra.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.sandydunlop.newsfeed.NewsfeedModInitializer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class CScrollable extends CContainer {
	private static final Identifier SCROLLER_TEXTURE = Identifier.of(NewsfeedModInitializer.MOD_ID,"widget/scroller");
    //private static final Identifier SCROLLER_TEXTURE = new Identifier(NewsfeedModInitializer.MOD_ID, "widget/scroller");
    protected final int innerHeight;
    private double scrollAmount;
    private boolean scrolling;
    private boolean renderBackground = true;
    private ItemCardContainer content = null;


    public CScrollable(CGUIScreen parent, int width, int widgetHeight, int y, int innerHeight) {
    	super(parent, 0, y, width, widgetHeight);
        //super(0, y, width, widgetHeight, ScreenTexts.EMPTY);
        this.innerHeight = innerHeight;
        parent.addDrawableChild(this);
    }
    

    public void setContent(ItemCardContainer widget) {
        this.content = widget;
        this.contentHeight = widget.getHeight();
        this.setHeight(this.contentHeight);

    }

    protected int getMaxPosition() {
        return this.innerHeight;
    }


    public int getRight()
    {
        return this.getX() + this.getWidth();
    }


    public int getBottom()
    {
        return this.getY() + this.getHeight();
    }
    
    //TODO:
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int j;
        int i;
        if (this.renderBackground) {
            i = 4;
            context.fillGradient(RenderLayer.getGuiOverlay(), this.getX(), this.getY(), this.getRight(), this.getY() + 4, Colors.RED, 0, 0);
            context.fillGradient(RenderLayer.getGuiOverlay(), this.getX(), this.getBottom() - 4, this.getRight(), this.getBottom(), 0, Colors.RED, 0);
        }
        if ((i = this.getMaxScroll()) > 0) {
            
        	j = this.getScrollbarPositionX();
            int k = (int)((float)(this.getHeight() * this.getHeight()) / (float)this.getMaxPosition());
            k = MathHelper.clamp((int)k, (int)32, (int)(this.getHeight() - 8));
            int l = (int)this.getScrollAmount() * (this.getHeight() - k) / i + this.getY();
            
            if (l < this.getY()) l = this.getY();
            
            context.fill(j, this.getY(), j + 6, this.getBottom(), -16777216);
            context.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_TEXTURE, j, l, 6, k);
        }
        //TODO:
        //RenderSystem.disableBlend();
    }

    public void enableScissor(DrawContext context) {
    	context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    public double getScrollAmount() {
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
        
    	return this.getWidth() - 6;
    }

    protected boolean isSelectButton(int button) {
        
    	return button == 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
    	if (button != 0) return false;
    	this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) return false;
        return this.scrolling;
    }

    //TODO
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
    	//TODO
        // if (this.getFocused() != null) {
        // 	this.getFocused().mouseReleased(mouseX, mouseY, button);
        // }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    	
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        
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