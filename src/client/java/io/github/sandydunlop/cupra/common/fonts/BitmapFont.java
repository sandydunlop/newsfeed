package io.github.sandydunlop.cupra.common.fonts;

import java.awt.image.BufferedImage;
import java.util.Map;

import io.github.sandydunlop.cupra.common.util.Dimension;


public class BitmapFont {
	public static class Glyph {
		public char glyph;
		public int x;
		public int y;
		public int width;
		
		public Glyph(char glyph, int x, int y, int w) {
			this.glyph = glyph;
			this.x = x;
			this.y = y;
			this.width = w;
		}
	}

	private FontSpec spec;
	private BufferedImage bitmap;
	private Map<Character, Glyph> glyphs;
	private int spaceWidth;
	private int emWidth = -1;


	public BitmapFont(FontSpec spec, BufferedImage bitmap, Map<Character, Glyph> glyphs) {
		this.spec = spec; //TODO: Call .duplicate() on this if fonts are getting mixed up
		this.bitmap = bitmap;
		this.glyphs = glyphs;
		this.spaceWidth = spec.getMonospaced() ? getEmWidth() : (int)(getEmWidth() * 0.5);
	}
	

	public BufferedImage getImage() {
		return bitmap;
	}
	

	public int getHeight() {
		return spec.getSize();
	}
	

	public Glyph getGlyph(char c) {
		return glyphs.get(c);
	}


	public int getEmWidth() {
		if (emWidth == -1){
			if (spec.getName().equals("basis33")){
				// TODO: Do this for all monospace fonts?
				emWidth = glyphs.get('M').width - 1;
			}else{
				emWidth = glyphs.get('M').width;
			}
		}
		return emWidth;
	}


	public int getSpaceWidth(){
		return spaceWidth;
	}
	

	public int stringWidth(String s) {
		final int length = s.length();
		if (spec.getMonospaced()){
			return length * getEmWidth();
		}
		int width = 0;
		for(int i = 0; i < length; i++) {
			Glyph g = getGlyph(s.charAt(i));
			if(g != null) {
				width += g.width;
			}
		}
		return width;
	}


    public Dimension stringDimensions(String text) {
		return getStringDimensions(text, -1);
	}


    public Dimension getStringDimensions(String text, int wrapAt) {
		return this.getStringDimensions(text, wrapAt, 0);
	}


	public int getDisplayableCharCount(String text, int containerWidth){
		int charX = 0;
		int charWidth = getEmWidth();
		int emWidth = getEmWidth();
		for (int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			if (!spec.getMonospaced()){
				if (c == ' '){
					charWidth = spaceWidth;
				}else{
					Glyph glyph = getGlyph(c);
					charWidth = glyph == null ? spaceWidth : glyph.width;
				}
				if (charX + (2*emWidth) > containerWidth){
					return i;
				}
			}else{
				charWidth = spaceWidth;
			}
			charX += charWidth;
		}
		return text.length();
	}


    public Dimension getStringDimensions(String text, int wrapAt, int startX) {
		int charX = startX == -1 ? 0 : startX;
		int charWidth = getEmWidth();
		int stringWidth = 0;
		int stringHeight = 0;
		int fontHeightExtra = 3;
		int widest = 0;
		for (int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			if (!spec.getMonospaced()){
				if (c == ' '){
					charWidth = spaceWidth;
				}else{
					Glyph glyph = getGlyph(c);
					charWidth = glyph == null ? spaceWidth : glyph.width;
				}
			}else{
				charWidth = spaceWidth;
			}
			if (wrapAt != -1 && charX + charWidth > wrapAt){
				charX = 0;
				stringHeight += getHeight() + fontHeightExtra;
			}
			if (charX + charWidth > widest){
				widest = charX  + charWidth;
			}
			charX += charWidth;
		}
		if (wrapAt == -1){
			stringWidth = charX;
		}else{
			 stringWidth = widest;
		}
        return new Dimension(stringWidth, stringHeight + getHeight(), charX, stringHeight);
    }
}
