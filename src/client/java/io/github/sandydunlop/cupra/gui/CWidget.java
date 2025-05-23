package io.github.sandydunlop.cupra.gui;


public class CWidget {
    private int x;
    private int y;
    private int width;
    private int height;


    public CWidget() {
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
