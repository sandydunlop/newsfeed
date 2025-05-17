package io.github.sandydunlop.newsfeed;

import java.util.List;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;


@Environment(EnvType.CLIENT)
public class MultiLineTextWidget extends AbstractTextWidget {
    private List<OrderedText> lines;
    private final TextRenderer textRenderer;
    private int x, y, height;


    public MultiLineTextWidget(List<OrderedText> lines, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Text.of(""), textRenderer);
        this.lines = lines;
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.height = height;
    }


    public void setX(int x) {
        this.x = x;
    }


    public void setY(int y) {
        this.y = y;
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public void setLines(List<OrderedText> lines) {
        this.lines = lines;
    }

    
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int lineY = y;
        for (OrderedText line : lines) {
            context.drawText(textRenderer, line, x, lineY, 0xFFFFFF, false);
            lineY += textRenderer.fontHeight;
            if (lineY > y + height) break;
        }
    }
}