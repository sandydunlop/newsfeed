package io.github.sandydunlop.newsfeed;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.rometools.rome.feed.synd.SyndEntry;

import io.github.sandydunlop.cupra.common.widgets.CButton;
import io.github.sandydunlop.cupra.common.widgets.CContainer;
import io.github.sandydunlop.cupra.common.widgets.CFormattedLabel;
import io.github.sandydunlop.cupra.common.widgets.CLabel;
import io.github.sandydunlop.cupra.common.widgets.CListBox;
import io.github.sandydunlop.cupra.common.widgets.CSpacer;
import io.github.sandydunlop.cupra.common.events.CListBoxSelectionChangedEvent;
import io.github.sandydunlop.cupra.common.events.CListBoxSelectionChangedListener;
import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;


public class NewsfeedArticleScreen extends Screen implements RssUpdateListener, CListBoxSelectionChangedListener {
	private static RssFeed rssFeed = null;
	private int articleIndex;
	private Article article;
	CLabel titleWidget;
	CListBox inbox;
	CFormattedLabel descriptionWidget;
	CButton prevButton;
	CButton nextButton;
	CButton openButton;
	CButton optionsButton;
	CButton closeButton;
	CContainer articleScreen;
	CContainer footer;


    public NewsfeedArticleScreen(Text title, Screen parent, RssFeed rssFeed) {
		super(title);
		NewsfeedArticleScreen.rssFeed = rssFeed;
	}


    @Override
	protected void init() {
		super.init();
		final int SMALL_VERTICAL_GAP = 5;
		
		articleScreen = new CContainer();

		inbox = new CListBox(articleScreen);

		new CSpacer(articleScreen, SMALL_VERTICAL_GAP);

		descriptionWidget = new CFormattedLabel(articleScreen);
		
		prevButton = new CButton(articleScreen, "newsfeed.article.prev.button", click -> {
			if (articleIndex > 0) {
				articleIndex--;
				article = Article.of(rssFeed.getEntry(articleIndex));
				selectArticle(article);
			}
		});
		this.addToFooter(prevButton);

		nextButton = new CButton(articleScreen, Text.translatable("newsfeed.article.next.button"), (btn) -> {
			if (articleIndex < rssFeed.usedEntries.size() - 1) {
				articleIndex++;
				article = Article.of(rssFeed.getEntry(articleIndex));
				selectArticle(article);
			}
		});
		this.addToFooter(nextButton);

		openButton = new CButton(articleScreen, Text.translatable("newsfeed.article.open.button"), (btn) -> {
			Util.getOperatingSystem().open(article.link);
		});
		this.addToFooter(openButton);

		optionsButton = new CButton(articleScreen, Text.translatable("newsfeed.article.options.button"), (btn) -> {
			Screen screen = NewsfeedClientModInitializer.getConfigScreen(this);
			MinecraftClient.getInstance().setScreen(screen);
		});
		this.addToFooter(optionsButton);

		closeButton = new CButton(articleScreen, Text.translatable("newsfeed.article.close.button"), (btn) -> {
			this.close();
		});
		this.addToFooter(closeButton);

		updateInbox();
		articleIndex = rssFeed.usedEntries.size() - 1;
		if (articleIndex > -1){
			article = Article.of(rssFeed.getEntry(articleIndex));
		}else{
			article = Article.empty() ;
		}
		populate(article);
		layout();
		rssFeed.addRssUpdateListener(this);
		inbox.addListBoxSelectionChangedListener(this);
    }


	private void populate(Article article)
	{
		if (article == null) {
			article = Article.empty();
		}
		descriptionWidget.setText(Text.of(article.title + "\n\n" + article.description));
		if (articleIndex == rssFeed.usedEntries.size() - 1) {
			nextButton.setEnabled(false);
		}else{
			nextButton.setEnabled(true);
		}
		if (articleIndex == 0) {
			prevButton.setEnabled(false);
		}else{
			prevButton.setEnabled(true);
		}
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


	@Override
	public void feedUpdated(RssUpdateEvent event) {
		updateInbox();
	}


	@Override
	public void selectionChanged(CListBoxSelectionChangedEvent event) {
		if (inbox.getSelectedOrNull() != null) {
			CListBoxEntry inboxSelection = inbox.getSelectedOrNull();
			SyndEntry selectedEntry = (SyndEntry)inboxSelection.getValue();
			for (SyndEntry entry : rssFeed.usedEntries) {
				if (entry.equals(selectedEntry)) {
					articleIndex = rssFeed.usedEntries.indexOf(entry);
					article = Article.of(entry);
					populate(article);
					return;
				}
			}
			article = Article.empty();
			populate(article);
		}		
	}


	private void updateInbox() {
		inbox.clear();
		if (rssFeed.usedEntries.size() > 0) {
			articleIndex = rssFeed.usedEntries.size() - 1;
			article = Article.of(rssFeed.getEntry(articleIndex));
			populate(article);
			for(int i=rssFeed.usedEntries.size() - 1; i >= 0; i--) {
				SyndEntry entry = rssFeed.usedEntries.get(i);
				CListBoxEntry listItem = new CListBoxEntry(entry.getTitle(), entry);
				inbox.addItem(listItem);
			}
		} else {
			article = Article.empty();
			populate(article);
		}
	}


	private void selectArticle(Article article) {
		if (article != null && article.getEntry() != null) {
			inbox.children().forEach(entry -> {
				if (entry instanceof CListBoxEntry listBoxEntry) {
					if (listBoxEntry.getValue().equals(article.getEntry())) {
						inbox.setSelected(listBoxEntry);
						inbox.scrollToSelected();
					}
				}
			});
		}
		inbox.setSelected(null);
	}
}
