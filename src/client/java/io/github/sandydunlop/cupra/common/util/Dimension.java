package io.github.sandydunlop.cupra.common.util;

public class Dimension {
    int width = -1;
    int height = -1;

    int canvasWidth = -1;
    int canvasHeight = -1;

    int nextCharX = -1;
    int nextCharY = -1;

    public Dimension(){
    }

    public Dimension(int width, int height, int nextCharX, int nextCharY){
        this.width = width;
        this.height = height;
        this.nextCharX = nextCharX;
        this.nextCharY = nextCharY;
    }

    public int getWidth(){
        return this.width;
    }

    public Dimension setWidth(int w){
        this.width = w;
        return this;
    }

    public int getHeight(){
        return this.height;
    }

    public Dimension setHeight(int h){
        this.height = h;
        return this;
    }

    public int getNextCharX(){
        return this.nextCharX;
    }

    public void setNextCharX(int nx){
        this.nextCharX = nx;
    }

    public int getNextCharY(){
        return this.nextCharY;
    }

    public void setNextCHarY(int ny){
        this.nextCharY = ny;
    }

    public void setCanvasWidth(int w){
        this.canvasWidth = w;
    }

    public int getCanvasWidth(){
        return this.canvasWidth;
    }

    public void setCanvasHeight(int h){
        this.canvasHeight = h;
    }

    public int getCanvasHeight(){
        return this.canvasHeight;
    }
}
