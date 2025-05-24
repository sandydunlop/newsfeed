package io.github.sandydunlop.newsfeed;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import io.github.sandydunlop.cupra.gui.CButton;
import io.github.sandydunlop.cupra.gui.CContainer;
import io.github.sandydunlop.cupra.gui.CLabel;
import io.github.sandydunlop.cupra.gui.CListBox;
import io.github.sandydunlop.cupra.gui.CMultiLineLabel;
import io.github.sandydunlop.cupra.gui.CSpacer;
import io.github.sandydunlop.cupra.gui.CGUIScreen;


public class NewsfeedArticleScreen extends CGUIScreen {
	private static RssFeed rssFeed = null;
	private int articleIndex;
	private Article article;

	CLabel titleWidget;
	CListBox inbox;
	CMultiLineLabel descriptionWidget;
	CButton prevButton;
	CButton nextButton;
	CButton openButton;
	CButton optionsButton;
	CButton closeButton;


    public NewsfeedArticleScreen(Text title, Screen parent, RssFeed rssFeed) {
		super(parent, title);
		NewsfeedArticleScreen.rssFeed = rssFeed;
	}


    @Override
	protected void init() {
		super.init();
		final int SMALL_VERTICAL_GAP = 5;
		this.setTextRenderer(MinecraftClient.getInstance().textRenderer);

		articleIndex = rssFeed.usedEntries.size() - 1;
		if (articleIndex > -1){
			article = Article.of(rssFeed.getEntry(articleIndex));
		}else{
			article = Article.empty() ;
		}

		titleWidget = new CLabel(this, Text.of(article.title));
		titleWidget.setTooltip(Tooltip.of(Text.of(article.title)));
		this.addToBody(titleWidget);

		inbox = new CListBox(this, Text.of(article.description));
		this.addToBody(inbox);

		this.addToBody(new CSpacer(SMALL_VERTICAL_GAP));

		descriptionWidget = new CMultiLineLabel(this, Text.of(article.description));
		this.addToBody(descriptionWidget);

		prevButton = new CButton(this, Text.translatable("newsfeed.article.prev.button"), (btn) -> {
			if (articleIndex > 0) {
				articleIndex--;
				article = Article.of(rssFeed.getEntry(articleIndex));
				titleWidget.setText(Text.of(article.title));
				titleWidget.setTooltip(Tooltip.of(Text.of(article.title)));
				descriptionWidget.setText(Text.of(article.description));
				if (articleIndex == 0) {
					btn.setEnabled(false);
				}else{
					btn.setEnabled(true);
				}
				nextButton.setEnabled(true);
			}
		});
		if (articleIndex == 0) {
			prevButton.setEnabled(false);
		}
		this.addToFooter(prevButton);

		nextButton = new CButton(this, Text.translatable("newsfeed.article.next.button"), (btn) -> {
			if (articleIndex < rssFeed.usedEntries.size() - 1) {
				articleIndex++;
				article = Article.of(rssFeed.getEntry(articleIndex));
				titleWidget.setText(Text.of(article.title));
				titleWidget.setTooltip(Tooltip.of(Text.of(article.title)));
				descriptionWidget.setText(Text.of(article.description));
				if (articleIndex == rssFeed.usedEntries.size() - 1) {
					btn.setEnabled(false);
				}else{
					btn.setEnabled(true);
				}
				prevButton.setEnabled(true);
			}
		});
		if (articleIndex == rssFeed.usedEntries.size() - 1) {
			nextButton.setEnabled(false);
		}
		this.addToFooter(nextButton);

		openButton = new CButton(this, Text.translatable("newsfeed.article.open.button"), (btn) -> {
			Util.getOperatingSystem().open(article.link);
		});
		this.addToFooter(openButton);

		optionsButton = new CButton(this, Text.translatable("newsfeed.article.options.button"), (btn) -> {
			Screen screen = NewsfeedClientModInitializer.getConfigScreen(this);
			MinecraftClient.getInstance().setScreen(screen);
		});
		this.addToFooter(optionsButton);

		closeButton = new CButton(this, Text.translatable("newsfeed.article.close.button"), (btn) -> {
			this.close();
		});
		this.addToFooter(closeButton);

		layout();
    }


    @Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
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
    }


	private void drawBackground(DrawContext context) {
		context.fill(0, 40, this.width, this.height - 40, 0x88000000);
		context.drawHorizontalLine(0, this.width, 40, 0xFF3F3F3F);
		context.drawHorizontalLine(0, this.width, 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 40, 0xFF3F3F3F);
	}

}
