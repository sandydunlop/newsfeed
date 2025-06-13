package io.github.sandydunlop.newsfeed;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import io.github.sandydunlop.cupra.common.fonts.BitmapFont;
import io.github.sandydunlop.cupra.common.fonts.BitmapFont.Glyph;
import io.github.sandydunlop.cupra.common.fonts.BitmapFontFactory;
import io.github.sandydunlop.cupra.common.fonts.FontSpec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;


public class Ticker {
	private DrawContext context = null;
	private FontSpec font = null;
    List<String> headlines = null;
    List<Segment> segments = null;
    int scrollAmount = 0;
    int maxScroll = 0;
    int gap = 40;


	public Ticker(DrawContext context, FontSpec font) {
        this.context = context;
        this.font = font;
	}


    public void display(String message) {
        if (headlines == null){
            headlines = new ArrayList<>();
        }
        headlines.add(0, message);
        if (headlines.size() > 4) {
            headlines.remove(0);
        }
        prepare();
    }


    public void prepare() {
        maxScroll = 0;
        BitmapFont bmf = BitmapFontFactory.load(font);
        segments = new ArrayList<>();
        for (int i = 0; i<headlines.size(); i++) {
            Segment segment = new Segment();
            String text = headlines.get(i);
            segment.width = bmf.stringWidth(text);
            segment.height = bmf.getHeight();
            BufferedImage segmentImage = new BufferedImage(segment.width, bmf.getHeight(), bmf.getImage().getType());
            int pos = 0, charWidth, spaceWidth = bmf.getEmWidth();
            for (int j = 0; j<text.length(); j++) {
                char c = text.charAt(j);
                if (c == ' '){
                    charWidth = spaceWidth;
                }else{
                    Glyph glyph = bmf.getGlyph(c);
                    charWidth = glyph == null ? spaceWidth : glyph.width;
                    if (glyph != null){
                        Graphics g = segmentImage.getGraphics();
                        g.drawImage(bmf.getImage(), 
                                pos, 0, pos+glyph.width, bmf.getHeight(), 
                                glyph.x, glyph.y, glyph.x + glyph.width, glyph.y+bmf.getHeight(), 
                                null);
                        g.dispose();
                    }
                }
                pos += charWidth;
            }
            segment.key = "ticker" + i;
            NativeImage nativeImage = new NativeImage(segment.width, bmf.getHeight(), false);
            NativeImageBackedTexture segmentTexture = new NativeImageBackedTexture(segment.key, segment.width, bmf.getHeight(), false);
            segmentTexture.setImage(nativeImage);
            drawToNativeImage(segmentTexture, segmentImage, 0, 0, segment.width, bmf.getHeight());
            Identifier identifier = Identifier.of(segment.key);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, segmentTexture);
            segmentTexture.upload();
            segments.add(segment);
            maxScroll += segment.width + gap;
        }
        scrollAmount = -context.getScaledWindowWidth();
    }


    public void render() {
        if (segments != null && !segments.isEmpty()){
    		context.fill(0, 0, context.getScaledWindowWidth(), 32, 0x77000000);
            int x = -scrollAmount;
            for (int i=0; i<segments.size(); i++) {
                try{
                    Segment segment = segments.get(i);
                    segment.x = x;
                    renderSegment(segment, x, 0);
                    x += segment.width + gap;
                }catch(Exception ignore){}
            }
            scrollAmount+=2;
            for (int i=segments.size()-1; i >= 0; i--) {
                Segment segment = segments.get(i);
                if (segment.x + segment.width < 0) {
                    segments.remove(i);
                    headlines.remove(i);
                }
            }
        }
    }


    private void renderSegment(Segment segment, int x, int y) {
        int sx = 0;
        int sw = segment.width;
        if (x < context.getScaledWindowWidth()) {
            if (x < 0) {
                sx = -x;
                sw = segment.width - sx;
            }
            Identifier identifier = Identifier.of(segment.key);
            context.drawTexture(RenderLayer::getGuiTextured, identifier, 
                    x+sx,y, 
                    sx,0, 
                    sw, segment.height, 
                    sw, segment.height, 
                    segment.width, segment.height);
        }
    }


    private void drawToNativeImage(NativeImageBackedTexture ni, BufferedImage bufferedImage, int x, int y, int width, int height) {
        for(int w = 0; w < width; w++) {
            for(int h = 0; h < height; h++) {
                if(w >= bufferedImage.getWidth() || h >= bufferedImage.getHeight()) continue;
                ni.getImage().setColorArgb(x + w, y + h, toArgb(bufferedImage.getRGB(w, h)));
            }
        }
    }


    private int toArgb(int rgb) {
        int a = (rgb >> 24) & 255;
        int b = (rgb >> 16) & 255;
        int g = (rgb >> 8) & 255;
        int r = (rgb) & 255;
        return a << 24 | b << 16 | g << 8 | r;
    }
}
