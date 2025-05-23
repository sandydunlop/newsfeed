package io.github.sandydunlop.cupra.gui;

import java.util.ArrayList;
import java.util.List;


public class CContainer extends CWidget {
	private List<CWidget> widgets;
    private boolean isHorizontal = false;
	private boolean isWide = false;
	final int WIDGET_HEIGHT = 20;
	final int MEDIUM_VERTICAL_GAP = 10;


    public CContainer() {
        init();
	}


    public CContainer(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        init();
	}


    public CContainer(CGUIScreen parent, int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
        init();
	}


	protected void init() {
		widgets = new ArrayList<>();
    }


    public void setIsHorizontal(boolean flag){
        isHorizontal = flag;
    }


	public void setIsWide(boolean flag){
		isWide = flag;
	}


	public boolean getIsWide(){
		return isWide;
	}


    public boolean getIsHorizontal(){
        return isHorizontal;
    }


    public List<CWidget> getWidgets(){
        return widgets;
    }


	public void add(CWidget widget){
		this.widgets.add(widget);
	}


    public void layout(){
        if (isHorizontal){
            horizontalLayout();
        }else{
            verticalLayout();
        }
    }


    private void horizontalLayout(){
        int footerButtonSpacing = 10;
		int layoutX;
        int layoutY = (int)((getHeight()/2) - 10);
        int widgetWidth = getWidth() / 4;
        if (widgets.size() > 3 || isWide){
            widgetWidth = (int)((getWidth()+footerButtonSpacing)/widgets.size());
        }
		int btnsWidth = widgetWidth * widgets.size();
		layoutX = (int)(getWidth()/2 - (btnsWidth/2)) + 10;
		Object[] widgetsArray = widgets.toArray();
		for (int i=0 ; i<widgetsArray.length ; i++) {
			CWidget widget = (CWidget)widgetsArray[i];
			if (!isWide){
				widget.setWidth(widgetWidth - footerButtonSpacing);
			}
			widget.setHeight(WIDGET_HEIGHT);
			widget.setX(layoutX + getX());
			widget.setY(layoutY + getY());
			if (i == widgetsArray.length - 1 && isWide){
				widget.setX(getWidth() - widgetWidth + getX());
			}
			layoutX += widgetWidth;
		}
    }


	private void verticalLayout(){
		final int screenHeight = getHeight();
		final int marginLeft = 0;
		final int marginTop = 0;
		final int bottomSectionHeight = 0;
		int nonexHeight = 0;
		List<CWidget> expandables = new ArrayList<>();
		for (CWidget widget : widgets) {
			if (widget instanceof CMultiLineTextBox){
				expandables.add(widget);
			}else if (widget instanceof CSpacer){
				nonexHeight += ((CSpacer)widget).getHeight();
			}else if (widget instanceof CContainer){
				nonexHeight += widget.getHeight();
			}else{
				widget.setHeight(WIDGET_HEIGHT);
				nonexHeight += WIDGET_HEIGHT;
			}
		}
		int expandableScreenHeight = screenHeight - marginTop - bottomSectionHeight - nonexHeight;
		for(CWidget widget : expandables) {
			widget.setHeight(expandableScreenHeight / expandables.size());
		}
		int layoutY = marginTop;
		for (CWidget widget : widgets) {
			widget.setWidth(getWidth());
			widget.setX(marginLeft + getX());
			widget.setY(layoutY + getY());
			widget.layout();
			layoutY += widget.getHeight();
		}
    }
}
