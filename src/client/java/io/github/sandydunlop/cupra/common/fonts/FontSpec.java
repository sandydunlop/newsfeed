package io.github.sandydunlop.cupra.common.fonts;


public class FontSpec {
    public enum UnderlineStyle {
        SOLID,
        DOTTED,
    }

    private String key = null;
    private boolean isKeyInvalidated = true;

    private String name = "basis33";
    private int size = 14;
    private int color = 0;
    private boolean shadow = false;
    private boolean antialias = true;
    private boolean monospaced = false;
    private boolean underline = false;
    private boolean strikethrough = false;
    private boolean bold = false;
    private boolean italic = false;
    private int underlineStyle;


    public FontSpec setName(String name){
        this.isKeyInvalidated = true;
        this.name = name;
        return this;
    }


    public FontSpec setSize(int size){
        this.isKeyInvalidated = true;
        this.size = size;
        return this;
    }


    public FontSpec setColor(int color){
        this.isKeyInvalidated = true;
        this.color = color;
        return this;
    }


    public FontSpec setShadow(boolean shadow){
        this.isKeyInvalidated = true;
        this.shadow = shadow;
        return this;
    }


    public FontSpec setAntialias(boolean antialias){
        this.isKeyInvalidated = true;
        this.antialias = antialias;
        return this;
    }


    public FontSpec setMonospaced(boolean monospaced){
        this.isKeyInvalidated = true;
        this.monospaced = monospaced;
        return this;
    }


    public FontSpec setUnderline(boolean underline){
        this.isKeyInvalidated = true;
        this.underline = underline;
        return this;
    }


    public FontSpec setStrikethrough(boolean strikethrough){
        this.isKeyInvalidated = true;
        this.strikethrough = strikethrough;
        return this;
    }


    public FontSpec setBold(boolean bold){
        this.isKeyInvalidated = true;
        this.bold = bold;
        return this;
    }


    public FontSpec setItalic(boolean italic){
        this.isKeyInvalidated = true;
        this.italic = italic;
        return this;
    }


    public String getName(){
        return this.name;
    }

    public int getSize(){
        return this.size;
    }


    public int getColor(){
        return this.color;
    }


    public boolean getShadow(){
        return this.shadow;
    }


    public boolean getAntialias(){
        return this.antialias;
    }


    public boolean getMonospaced(){
        return this.monospaced;
    }


    public boolean getUnderline(){
        return this.underline;
    }


    public boolean getStrikethrough(){
        return this.strikethrough;
    }


    public boolean getBold(){
        return this.bold;
    }


    public boolean getItalic(){
        return this.italic;
    }


    public void setUnderlineStyle(int style) {
        this.underlineStyle = style;
    }


    public int getUnderlineStyle() {
        return this.underlineStyle;
    }


    public String getKey(){
        if (this.isKeyInvalidated && this.name != null){
		    this.key = String.format("%s_%d_%d_%d%d%d%d%d",
				this.name,
				this.color,
				this.size,
				antialias?1:0,
				underline?1:0,
				strikethrough?1:0,
				bold?1:0,
				italic?1:0).toLowerCase().replaceAll("[^a-z0-9]", "_");
            this.isKeyInvalidated = false;
        }
        return this.key;
    }


    public FontSpec duplicate(){
        FontSpec fontOptions = new FontSpec();
        fontOptions.setName(name);
        fontOptions.setSize(size);
        fontOptions.setColor(color);
        fontOptions.setShadow(shadow);
        fontOptions.setAntialias(antialias);
        fontOptions.setMonospaced(monospaced);
        fontOptions.setUnderline(underline);
        fontOptions.setStrikethrough(strikethrough);
        fontOptions.setBold(bold);
        fontOptions.setItalic(italic);
        return fontOptions;
    }


    public FontSpec mergeFrom(FontSpec from){
        if (from.getName() != null){
            this.name = from.getName();
        }
        this.size = from.getSize();
        this.color = from.getColor();
        this.shadow = from.getShadow();
        this.antialias = from.getAntialias();
        this.monospaced = from.getMonospaced();
        this.underline = from.getUnderline();
        this.strikethrough = from.getStrikethrough();
        this.bold = from.getBold();
        this.italic = from.getItalic();
        this.isKeyInvalidated = true;
        return this;
    }
}
