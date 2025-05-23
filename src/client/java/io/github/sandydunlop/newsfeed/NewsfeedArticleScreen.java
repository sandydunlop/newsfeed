package io.github.sandydunlop.newsfeed;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import io.github.sandydunlop.cupra.gui.CLabel;
import io.github.sandydunlop.cupra.gui.CMultiLineTextBox;
import io.github.sandydunlop.cupra.gui.CGUIScreen;


public class NewsfeedArticleScreen extends CGUIScreen {
	private static RssFeed rssFeed = null;
	private int articleIndex;
	private Article article;

	CMultiLineTextBox descriptionWidget;
	CLabel titleWidget;
	ButtonWidget prevButton;
	ButtonWidget nextButton;
	ButtonWidget openButton;
	ButtonWidget optionsButton;
	ButtonWidget closeButton;


    public NewsfeedArticleScreen(Text title, Screen parent, RssFeed rssFeed) {
		super(title, parent);
		NewsfeedArticleScreen.rssFeed = rssFeed;
	}


    @Override
	protected void init() {
		super.init();
		final int WIDGET_HEIGHT = 20;
		final int MEDIUM_VERTICAL_GAP = 10;
		final int screenWidth = this.width;
		final int screenHeight = this.height;
		final int marginLeft = (int)(screenWidth * 0.1);
		int y = 60;

		this.setTextRenderer(MinecraftClient.getInstance().textRenderer);

		articleIndex = rssFeed.usedEntries.size() - 1;
		if (articleIndex > -1){
			article = Article.of(rssFeed.getEntry(articleIndex));
		}else{
			article = Article.empty() ;
		}

		titleWidget = new CLabel(this, Text.of(article.title));
		titleWidget.setTooltip(Tooltip.of(Text.of(article.title)));
		descriptionWidget = new CMultiLineTextBox(this, Text.of(article.description));

		layout();

		int buttonCount = 5;
		int buttonPadding = 5;
		int buttonWidth = (screenWidth - (marginLeft*2) + buttonPadding) / buttonCount;
		int buttonX = marginLeft;
		y = screenHeight - WIDGET_HEIGHT - MEDIUM_VERTICAL_GAP;

		prevButton = ButtonWidget.builder(Text.translatable("newsfeed.article.prev.button"), (btn) -> {
			if (articleIndex > 0) {
				articleIndex--;
				article = Article.of(rssFeed.getEntry(articleIndex));
				titleWidget.setText(Text.of(article.title));
				titleWidget.setTooltip(Tooltip.of(Text.of(article.title)));
				descriptionWidget.setText(Text.of(article.description));
				if (articleIndex == 0) {
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
		if (articleIndex == 0) {
			prevButton.active = false;
		}
		this.addDrawableChild(prevButton);
		buttonX += buttonWidth;

		nextButton = ButtonWidget.builder(Text.translatable("newsfeed.article.next.button"), (btn) -> {
			if (articleIndex < rssFeed.usedEntries.size() - 1) {
				articleIndex++;
				article = Article.of(rssFeed.getEntry(articleIndex));
				titleWidget.setText(Text.of(article.title));
				titleWidget.setTooltip(Tooltip.of(Text.of(article.title)));
				descriptionWidget.setText(Text.of(article.description));
				if (articleIndex == rssFeed.usedEntries.size() - 1) {
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
		if (articleIndex == rssFeed.usedEntries.size() - 1) {
			nextButton.active = false;
		}
		this.addDrawableChild(nextButton);
		buttonX += buttonWidth;

		openButton = ButtonWidget.builder(Text.translatable("newsfeed.article.open.button"), (btn) -> {
			Util.getOperatingSystem().open(article.link);
		}).build();
		openButton.setWidth(buttonWidth - buttonPadding);
		openButton.setHeight(WIDGET_HEIGHT);
		openButton.setX(buttonX);
		openButton.setY(y);
		this.addDrawableChild(openButton);
		buttonX += buttonWidth;

		optionsButton = ButtonWidget.builder(Text.translatable("newsfeed.article.options.button"), (btn) -> {
			Screen screen = NewsfeedClientModInitializer.getConfigScreen(this);
			MinecraftClient.getInstance().setScreen(screen);
		}).build();
		optionsButton.setWidth(buttonWidth - buttonPadding);
		optionsButton.setHeight(WIDGET_HEIGHT);
		optionsButton.setX(buttonX);
		optionsButton.setY(y);
		this.addDrawableChild(optionsButton);
		buttonX += buttonWidth;

		closeButton = ButtonWidget.builder(Text.translatable("newsfeed.article.close.button"), (btn) -> {
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

		//context.fill(marginLeft, 80, this.width - marginLeft, this.height - 40, 0x88303030);
    }


	private void drawBackground(DrawContext context) {
		context.fill(0, 40, this.width, this.height - 40, 0x88000000);
		context.drawHorizontalLine(0, this.width, 40, 0xFF3F3F3F);
		context.drawHorizontalLine(0, this.width, 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 40, 0xFF3F3F3F);
	}

}
