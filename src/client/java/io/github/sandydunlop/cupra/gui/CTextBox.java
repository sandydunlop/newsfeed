package io.github.sandydunlop.cupra.gui;

import io.github.sandydunlop.newsfeed.ClipboardHelper;
import io.github.sandydunlop.newsfeed.NewsfeedModInitializer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class CTextBox extends CWidget {
    private CGUIScreen screen;
    private TextFieldWidget widget = null;
    private TextIconButtonWidget clearButton = null;
    private TextIconButtonWidget pasteButton = null;
    private final int WIDGET_SIZE = 20;


    public CTextBox(CGUIScreen screen, String text) {
        this.screen = screen;
        System.out.println("TextBox.CTextBox");
        widget = new TextFieldWidget(screen.getTextRenderer(), 100, WIDGET_SIZE, Text.of(""));
        widget.setEditable(true);
        widget.setMaxLength(200);
        widget.setVisible(true);
        widget.setText(text);
		screen.addDrawableChild(widget);
    }


    public void setText(String text) {
        System.out.println("TextBox.setText: " + text);
        widget.setMessage(Text.of(text));
    }


    public void setTooltip(Tooltip tooltip){
        widget.setTooltip(tooltip);
    }

    
    public void setX(int x) {
        super.setX(x);
        widget.setX(x);
    }


    public void setY(int y) {
        super.setY(y);
        widget.setY(y);
    }


    public void setWidth(int width) {
        super.setWidth(width);
        widget.setWidth(width);
    }
    

    public void setHeight(int height) {
        widget.setHeight(height);
    }


    public int getHeight() {
        return widget.getHeight();
    }


    public String getText() {
        return widget.getText();
    }


    public void layout(){
        int buttonsWidth = 0;
        if (clearButton != null){
            buttonsWidth += WIDGET_SIZE;
        }
        if (pasteButton != null){
            buttonsWidth += WIDGET_SIZE;
        }
        widget.setWidth(getWidth() - buttonsWidth);

        int buttonX = this.getX() + this.getWidth() - buttonsWidth;
        if (clearButton != null){
            clearButton.setX(buttonX);
            clearButton.setY(getY());
            buttonX += WIDGET_SIZE;
        }
        if (pasteButton != null){
            pasteButton.setX(buttonX);
            pasteButton.setY(getY());
            buttonX += WIDGET_SIZE;
        }
    }


    public void addClearButton(){
	    clearButton = TextIconButtonWidget.builder(Text.of(""), (btn) ->{ 
				this.setText("");
			}, true)
			.texture(Identifier.of(NewsfeedModInitializer.MOD_ID,"icon/clear"), 16, 16)
			.dimension(WIDGET_SIZE, WIDGET_SIZE).build();
		clearButton.setTooltip(Tooltip.of(Text.translatable("newsfeed.config.clear.tooltip")));
		screen.addDrawableChild(clearButton);
    }


    public void addPasteButton(){
	    pasteButton = TextIconButtonWidget.builder(Text.of(""), (btn) ->{ 
				String text = ClipboardHelper.getClipboardText();
				this.setText(text);				
			}, true)
			.texture(Identifier.of(NewsfeedModInitializer.MOD_ID,"icon/paste"), 16, 16)
			.dimension(WIDGET_SIZE, WIDGET_SIZE).build();
		pasteButton.setTooltip(Tooltip.of(Text.translatable("newsfeed.config.paste.tooltip")));
		screen.addDrawableChild(pasteButton);
    }
}
