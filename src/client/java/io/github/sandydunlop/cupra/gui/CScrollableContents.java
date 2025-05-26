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
	List<ItemCard> cards = new ArrayList<ItemCard>();


	public CScrollableContents(int itemHeight) {
        this.itemHeight = itemHeight;
        this.height = 0;
	}


    public void setPos(int x, int y){
        originX = x;
        originY = y;
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


	public void addItem(ItemCard itemcard) {
        height += itemHeight;
        itemcard.setHeight(itemHeight);;
		cards.add(itemcard);
	}


	public int getHeight() {
        return height;
	}


	public void renderAll(int scroll, DrawContext context, int mouseX, int mouseY, float delta) {
        int i = 0;
		for (ItemCard card : cards) {
            int x = originX;
            int y = originY + (itemHeight*i);
            card.setX(x);
            card.setY(y);
            card.setWidth(width);
            scroll = (int)(scrollAmount);
			card.renderF(scroll, context, mouseX, mouseY, delta);
            i++;
		}
	}
}