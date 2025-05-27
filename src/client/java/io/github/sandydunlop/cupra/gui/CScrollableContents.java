package io.github.sandydunlop.cupra.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;


public class CScrollableContents {
	int originX;
	int originY;
	int padding = 25; //TODO: Make this work
	int width = 0;
    int height = 0;
    int itemHeight = 0;
    double scrollAmount = 0;
	List<CListBoxEntry> items = new ArrayList<CListBoxEntry>();
    CListBoxEntry selected = null;
    int selectedIndex = -1;


	public CScrollableContents(int itemHeight) {
        this.itemHeight = itemHeight;
        this.height = 0;
	}


    public void setPos(int x, int y){
        originX = x;
        originY = y;
    }


    public List<CListBoxEntry> children() {
        return items;
    }


    public double getScrollAmount() {
        return scrollAmount;
    }


    public void setScrollAmount(double scrollAmount) {
        this.scrollAmount = scrollAmount;
    }


    public void setWidth(int width){
        this.width = width;
    }


	public void addItem(CListBoxEntry itemcard) {
        height += itemHeight;
        itemcard.setHeight(itemHeight);;
		items.add(itemcard);
	}


	public int getHeight() {
        return height;
	}


    public void clear() {
        items.clear();
        height = 0;
        selected = null;
        selectedIndex = -1;
    }


    public void setSelected(CListBoxEntry selected) {
        this.selected = selected;
		for (CListBoxEntry entry : items) {
            if (entry.equals(selected)) {
                selectedIndex = items.indexOf(entry);
                break;
            }
        }
    }


    public CListBoxEntry getSelected() {
        return selected;
    }


	public void renderAll(int scroll, DrawContext context, int mouseX, int mouseY, float delta) {
        for (int i=0; i<items.size(); i++) {
            CListBoxEntry card = items.get(i);
            int x = originX;
            int y = originY + (itemHeight*i);
            card.setX(x);
            card.setY(y);
            card.setWidth(width);
            scroll = (int)(scrollAmount);
            boolean isSelected = i == selectedIndex;
			card.renderF(scroll, context, mouseX, mouseY, delta, isSelected);
		}
	}
}