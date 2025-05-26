package io.github.sandydunlop.cupra.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;


public class ItemCardContainer {

	int originX = -55;
	int originY;

	int padding = 25;
	int width;
    int _height;
	int cardWidth = 85;
	int cardHeight = 135;

	int w = cardWidth + padding;
	int h = cardHeight + padding;

	int qw;

	List<ItemCard> cards = new ArrayList<ItemCard>();

	public ItemCardContainer(int width, int y) {
		this.width = width;
        this._height = 50;//TODO: What
		qw = width / w;
		originY = y + 10;
	}

    public void setPos(int x, int y){
        System.out.println("Setting origin to " + x + ", " + y);
        originX = x;
        originY = y;
    }

    public void setSize(int width, int height){
        System.out.println("Setting size to " + width + ", " + height);
        this.width = width;
        this._height = height;
    }

	public void addItem(ItemCard itemcard) {
		itemcard.init(getCoordinates(), cardWidth, cardHeight);
		cards.add(itemcard);
	}

	public int getHeight() {
		return h * (cards.size() / qw);
	}

	private int[] getCoordinates() {
		int x = originX + w * ((cards.size() % qw) + 1);
		int y = originY + h * (cards.size() / qw);
		return new int[] {x, y};
	}

	public void renderAll(int scroll, DrawContext context, int mouseX, int mouseY, float delta) {
        int i = 0;
		for (ItemCard card : cards) {
            int x = originX;
            int y = originY + (20*i);
            //System.out.println("Setting card " + i + " to " + x + ", " + y);
            card.setX(x);
            card.setY(y);
            card.setWidth(width);
			card.renderF(scroll, context, mouseX, mouseY, delta);
            i++;
		}
	}
}