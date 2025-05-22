package io.github.sandydunlop.newsfeed;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.rometools.rome.feed.synd.SyndEntry;


public class NewsfeedArticleScreen extends Screen {
	private Screen parent;
	private static RssFeed rssFeed = null;
	private SyndEntry entry;
	private int currentEntry;
	private String title;
	private String description;

	List<OrderedText> wrappedDescription;
	MultiLineTextWidget descriptionWidget;
	TextWidget titleWidget;
	ButtonWidget prevButton;
	ButtonWidget nextButton;


    public NewsfeedArticleScreen(Text title, Screen parent, RssFeed rssFeed) {
		super(title);
		this.parent = parent;
		NewsfeedArticleScreen.rssFeed = rssFeed;
	}


    @Override
	protected void init() {
		final int WIDGET_HEIGHT = 20;
		final int MEDIUM_VERTICAL_GAP = 10;
		final int screenWidth = this.width;
		final int screenHeight = this.height;
		final int marginLeft = (int)(screenWidth * 0.1);
		int y = 60;
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		title = "";
		description = "";
		currentEntry = rssFeed.usedEntries.size() - 1;
		if (currentEntry > -1){
        	entry = rssFeed.getEntry(currentEntry);
			title = entry.getTitle();
			if (entry.getDescription() == null){
				description = "";
			}else{
				description = entry.getDescription().getValue();
			}
		}else{
			entry = null;
		}

		int titleLabelWidth = (int)(screenWidth * 0.8);
		int titleLabelHeight = WIDGET_HEIGHT;
		titleWidget = new TextWidget(titleLabelWidth, titleLabelHeight,Text.of(title), textRenderer);
		titleWidget.setX(marginLeft);
		titleWidget.setY(y);
		titleWidget.alignLeft();
		titleWidget.setTooltip(Tooltip.of(Text.of(title)));
		this.addDrawableChild(titleWidget);
		y+= WIDGET_HEIGHT;

		int descriptionLabelWidth = (int)(screenWidth * 0.8);
		int descriptionLabelHeight = screenHeight - y - 40;
		int descriptionPadding = 2;
		wrappedDescription = textRenderer.wrapLines(Text.of(description), descriptionLabelWidth);
		descriptionWidget = new MultiLineTextWidget(wrappedDescription, textRenderer, marginLeft, y, descriptionLabelWidth, descriptionLabelHeight);
		descriptionWidget.setX(marginLeft + descriptionPadding);
		descriptionWidget.setY(y + descriptionPadding);
		descriptionWidget.setWidth(descriptionLabelWidth - (descriptionPadding * 2));
		descriptionWidget.setHeight(descriptionLabelHeight - (descriptionPadding * 2));
		this.addDrawableChild(descriptionWidget);
		y+= WIDGET_HEIGHT * 4;

		int buttonCount = 5;
		int buttonPadding = 5;
		int buttonWidth = (screenWidth - (marginLeft*2) + buttonPadding) / buttonCount;
		int buttonX = marginLeft;
		y = screenHeight - WIDGET_HEIGHT - MEDIUM_VERTICAL_GAP;

		prevButton = ButtonWidget.builder(Text.translatable("newsfeed.article.prev.button"), (btn) -> {
			if (currentEntry > 0) {
				currentEntry--;
				entry = rssFeed.getEntry(currentEntry);
				titleWidget.setMessage(Text.of(entry.getTitle()));
				titleWidget.setTooltip(Tooltip.of(Text.of(entry.getTitle())));
				if (entry.getDescription() == null){
					description = "";
				}else{
					description = entry.getDescription().getValue();
				}
				wrappedDescription = textRenderer.wrapLines(Text.of(description), descriptionLabelWidth);
				descriptionWidget.setLines(wrappedDescription);
				if (currentEntry == 0) {
					btn.active = false;
				}else{
					btn.active = true;
				}
				nextButton.active = true;
			}
		}).build();
		prevButton.setWidth(buttonWidth - buttonPadding);
		prevButton.setHeight(WIDGET_HEIGHT);
		prevButton.setX(buttonX);
		prevButton.setY(y);
		if (currentEntry == 0) {
			prevButton.active = false;
		}
		this.addDrawableChild(prevButton);
		buttonX += buttonWidth;

		nextButton = ButtonWidget.builder(Text.translatable("newsfeed.article.next.button"), (btn) -> {
			if (currentEntry < rssFeed.usedEntries.size() - 1) {
				currentEntry++;
				entry = rssFeed.getEntry(currentEntry);
				titleWidget.setMessage(Text.of(entry.getTitle()));
				titleWidget.setTooltip(Tooltip.of(Text.of(entry.getTitle())));
				if (entry.getDescription() == null){
					description = "";
				}else{
					description = entry.getDescription().getValue();
				}
				wrappedDescription = textRenderer.wrapLines(Text.of(description), descriptionLabelWidth);
				descriptionWidget.setLines(wrappedDescription);
				if (currentEntry == rssFeed.usedEntries.size() - 1) {
					btn.active = false;
				}else{
					btn.active = true;
				}
				prevButton.active = true;
			}
		}).build();
		nextButton.setWidth(buttonWidth - buttonPadding);
		nextButton.setHeight(WIDGET_HEIGHT);
		nextButton.setX(buttonX);
		nextButton.setY(y);
		if (currentEntry == rssFeed.usedEntries.size() - 1) {
			nextButton.active = false;
		}
		this.addDrawableChild(nextButton);
		buttonX += buttonWidth;

		ButtonWidget openButton = ButtonWidget.builder(Text.translatable("newsfeed.article.open.button"), (btn) -> {
			Util.getOperatingSystem().open(entry.getLink());
		}).build();
		openButton.setWidth(buttonWidth - buttonPadding);
		openButton.setHeight(WIDGET_HEIGHT);
		openButton.setX(buttonX);
		openButton.setY(y);
		this.addDrawableChild(openButton);
		buttonX += buttonWidth;

		ButtonWidget optionsButton = ButtonWidget.builder(Text.translatable("newsfeed.article.options.button"), (btn) -> {
			Screen screen = NewsfeedClientModInitializer.getConfigScreen(this);
			MinecraftClient.getInstance().setScreen(screen);
		}).build();
		optionsButton.setWidth(buttonWidth - buttonPadding);
		optionsButton.setHeight(WIDGET_HEIGHT);
		optionsButton.setX(buttonX);
		optionsButton.setY(y);
		this.addDrawableChild(optionsButton);
		buttonX += buttonWidth;

		ButtonWidget closeButton = ButtonWidget.builder(Text.translatable("newsfeed.article.close.button"), (btn) -> {
			this.close();
		}).build();
		closeButton.setWidth(buttonWidth - buttonPadding);
		closeButton.setHeight(WIDGET_HEIGHT);
		closeButton.setX(buttonX);
		closeButton.setY(y);
		this.addDrawableChild(closeButton);
		buttonX += buttonWidth;
    }


    @Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		final int marginLeft = (int)(this.width * 0.1);

		if (client.player != null){
			this.applyBlur();
			this.renderInGameBackground(context);
		}
		super.render(context, mouseX, mouseY, delta);
		if (client.player == null){
			drawBackground(context);
		}

		// Logo and title
		int logoTop = 5;
		int logoLeft = (int)(this.width * 0.1);
		Identifier texture = Identifier.of(NewsfeedModInitializer.MOD_ID, "icon-32.png");
		context.drawTexture(RenderLayer::getGuiTextured, texture, logoLeft, logoTop, 0, 0, 32, 32, 32, 32);
		context.getMatrices().push();  
		context.getMatrices().scale(2.0F, 2.0F, 1F);  

		Text text = Text.translatable("newsfeed.config.title");
		if (rssFeed.feedTitle != null && !rssFeed.feedTitle.isEmpty()){
			text = Text.of(rssFeed.feedTitle);
		}
		context.drawText(client.textRenderer, text, (logoLeft + 40) / 2, logoTop + 1, 0xFFFFFFFF, true);
		context.getMatrices().pop();

		context.fill(marginLeft, 80, this.width - marginLeft, this.height - 40, 0x88303030);
    }


	private void drawBackground(DrawContext context) {
		context.fill(0, 40, this.width, this.height - 40, 0x88000000);
		context.drawHorizontalLine(0, this.width, 40, 0xFF3F3F3F);
		context.drawHorizontalLine(0, this.width, 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 40, 0xFF3F3F3F);
	}


	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
