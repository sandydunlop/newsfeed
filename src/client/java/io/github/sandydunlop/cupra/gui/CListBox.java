package io.github.sandydunlop.cupra.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.github.sandydunlop.cupra.gui.events.CListBoxSelectionChangedEvent;
import io.github.sandydunlop.cupra.gui.events.CListBoxSelectionChangedListener;
import io.github.sandydunlop.newsfeed.NewsfeedModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class CListBox extends CWidget {
    private final int ITEM_PADDING = 2;
	private static final Identifier SCROLLER_TEXTURE = Identifier.of(NewsfeedModInitializer.MOD_ID,"widget/scroller");
    private double scrollAmount;
    private boolean scrolling;
    private CScrollableContents content;
    CGUIScreen parent = null;
    int headerHeight = 0; //TODO: Make this work
	protected int contentHeight = 0;
	private List<CListBoxSelectionChangedListener> listeners = new ArrayList<>();


    public CListBox(CGUIScreen parent) {
        this.parent = parent;
        parent.addDrawableChild(this);
		content = new CScrollableContents(MinecraftClient.getInstance().textRenderer.fontHeight + ITEM_PADDING*2);
    }
    

    public void setContent(CScrollableContents widget) {
        this.content = widget;
        this.contentHeight = widget.getHeight();
        this.setHeight(this.contentHeight);
    }


    public void setSelected(CListBoxEntry entry) {
        content.setSelected(entry);
        CListBoxSelectionChangedEvent event = new CListBoxSelectionChangedEvent(this);
        fireListBoxSelectionChangedEvent(event);
    }


    public void scrollToSelected(){
        CListBoxEntry selected = content.selected;
        if (selected != null && selected.getY() - content.scrollAmount< this.getY()) {
            this.scrollAmount = Math.max(0, selected.getY() - this.getY());
        }
        if (selected != null && selected.getY() - content.scrollAmount + selected.getHeight() > this.getBottom()) {
            this.scrollAmount = Math.min(getMaxScroll(), selected.getY() - this.getY() - this.getHeight() + selected.getHeight() );
        }
    }


    public int getContentHeight()
    {
        return this.contentHeight;
    }
    
    
    protected int getMaxPosition() {
        return this.content.height;
    }


    public int getRight() {
        return this.getX() + this.getWidth();
    }


    public int getBottom() {
        return this.getY() + this.getHeight();
    }


	public void addItem(CListBoxEntry item) {
        content.addItem(item);
        if (content.children().size() == 1) {
            content.setSelected(item);
        }
    }


    public void clear() {
        content.clear();
        this.scrollAmount = 0.0;
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
        enableScissor(context);
        content.renderAll((int)getScrollAmount(), context, mouseX, mouseY, delta);
        context.disableScissor();
    }


    public void enableScissor(DrawContext context) {
    	context.enableScissor(this.getX(), this.getY(), this.getRight() - 6, this.getBottom());
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


    public List<CListBoxEntry> children() {
        return this.content.children();
    }


    @Nullable
    protected final CListBoxEntry getEntryAtPosition(double x, double y) {
        int i = this.getWidth() / 2;

        int j = this.getX() + this.getWidth() / 2;
        int k = j - i;
        int l = j + i;
        //int m = MathHelper.floor(y - (double)this.getY()) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int m = MathHelper.floor(y - (double)this.getY()) - this.headerHeight + (int)this.getScrollAmount();
        int n = m / content.itemHeight;
        if (x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < children().size()) {
            return this.children().get(n);
        }
        return null;
    }


    @Nullable
    public CListBoxEntry getSelectedOrNull() {
        return content.selected;
    }
    
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
    	if (button != 0) return false;
    	this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) return false;

        if (mouseX < (double)this.getScrollbarPositionX()) {
            // If the click is not on the scrollbar, we handle it as a normal click
            CListBoxEntry entry = this.getEntryAtPosition(mouseX, mouseY);
            if (entry != null) {
                {
                    setSelected(entry);
                    scrollToSelected();
                }
                return true;
            }
        }
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
        this.setScrollAmount(this.getScrollAmount() - verticalAmount * 0.25 * (double)this.content.height / 2.0);
        return true;
    }


    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.getY() && mouseY <= (double)this.getBottom() && mouseX >= (double)this.getX() && mouseX <= (double)this.getRight();
    }


	public void removeListBoxSelectionChangedListener(CListBoxSelectionChangedListener listener) {
		listeners.remove(listener);
	}


	public void addListBoxSelectionChangedListener(CListBoxSelectionChangedListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}


	private void fireListBoxSelectionChangedEvent(CListBoxSelectionChangedEvent event) {
		for (CListBoxSelectionChangedListener listener : listeners) {
			listener.selectionChanged(event);
		}
	}
}