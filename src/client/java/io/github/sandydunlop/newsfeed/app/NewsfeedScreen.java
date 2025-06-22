package io.github.sandydunlop.newsfeed.app;

import com.rometools.rome.feed.synd.SyndEntry;

import io.github.sandydunlop.cupra.common.CupraScreen;
import io.github.sandydunlop.cupra.common.util.Align;
import io.github.sandydunlop.cupra.common.widgets.CButton;
import io.github.sandydunlop.cupra.common.widgets.CContainer;
import io.github.sandydunlop.cupra.common.widgets.CFormattedLabel;
import io.github.sandydunlop.cupra.common.widgets.CLabel;
import io.github.sandydunlop.cupra.common.widgets.CListBox;
import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;
import io.github.sandydunlop.cupra.common.widgets.CSpacer;
import io.github.sandydunlop.cupra.platform.PlatformServices;


public class NewsfeedScreen extends CupraScreen implements RssUpdateListener {
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
	CContainer header;
	CContainer body;
	CContainer footer;

    
	public NewsfeedScreen(){
		final int SMALL_VERTICAL_GAP = 5;
        this.setPadding(5);
        this.setAlignHorizontal(Align.Horizontal.SPREAD);

        rssFeed = new RssFeed();

        header = new CContainer(this, true);
        header.setPadding(4);

        body = new CContainer(this);
        body.setPadding(0);
        body.setExpandable(true);

        footer = new CContainer(this, true);
        footer.setAlignHorizontal(Align.Horizontal.SPREAD);  //TODO: Make this work
        footer.setPadding(10);

        titleWidget = new CLabel(header, "")
                .fontSize(22)
                .bold();
        titleWidget.setHeight(28);

        inbox = new CListBox(body, selectionChanged -> {
			SyndEntry selectedEntry = (SyndEntry)selectionChanged.getValue();
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
        });
        inbox.setExpandable(true);

		new CSpacer(body, SMALL_VERTICAL_GAP);
        descriptionWidget = new CFormattedLabel(body);
        descriptionWidget.setExpandable(true);

		prevButton = new CButton(footer, "Prev", click -> {
			if (articleIndex > 0) {
				articleIndex--;
				article = Article.of(rssFeed.getEntry(articleIndex));
				selectArticle(article);
			}
		});

		nextButton = new CButton(footer, "Next", click -> {
			if (articleIndex < rssFeed.usedEntries.size() - 1) {
				articleIndex++;
				article = Article.of(rssFeed.getEntry(articleIndex));
				selectArticle(article);
			}
		});

		openButton = new CButton(footer, "Open Link", click -> {
			CListBoxEntry selected = inbox.getSelected();
            SyndEntry selectedEntry = (SyndEntry)selected.getValue();
            String address = selectedEntry.getLink();
            PlatformServices.getInstance().openLinkInBrowser(address);
		});

		optionsButton = new CButton(footer, "Options", click -> {
			// Screen screen = NewsfeedClientModInitializer.getConfigScreen(this);
			// MinecraftClient.getInstance().setScreen(screen);
            PlatformServices.getInstance().showNotification("Hello");
		});

		closeButton = new CButton(footer, "Close", click -> {
			this.close();
		});

		updateInbox();
		articleIndex = rssFeed.usedEntries.size() - 1;
		if (articleIndex > -1){
			article = Article.of(rssFeed.getEntry(articleIndex));
		}else{
			article = Article.empty() ;
		}
		populate(article);
		rssFeed.addRssUpdateListener(this);
        rssFeed.init();
        rssFeed.update();
    }


	private void populate(Article article)
	{
		if (article == null) {
			article = Article.empty();
		}
        titleWidget.setText(rssFeed.feedTitle);
        CLabel heading = new CLabel(null, article.title).bold();
        descriptionWidget.clear();
        descriptionWidget.add(heading);
        descriptionWidget.add(CFormattedLabel.newLine());
        descriptionWidget.add(CFormattedLabel.newLine());
        descriptionWidget.add(new CLabel(null, article.description));
        descriptionWidget.layout();

        nextButton.setEnabled(articleIndex != rssFeed.usedEntries.size() - 1);
        prevButton.setEnabled(articleIndex != 0);
    }



	@Override
	public void feedUpdated(RssUpdateEvent event) {
		updateInbox();
	}


	private void updateInbox() {
		inbox.clear();
		if (!rssFeed.usedEntries.isEmpty()) {
			articleIndex = rssFeed.usedEntries.size() - 1;
			article = Article.of(rssFeed.getEntry(articleIndex));
			populate(article);
			for(int i=rssFeed.usedEntries.size() - 1; i >= 0; i--) {
				SyndEntry entry = rssFeed.usedEntries.get(i);
				CListBoxEntry listItem = new CListBoxEntry(entry.getTitle(), entry.getLink(), entry);
				inbox.add(listItem);
			}
		} else {
			article = Article.empty();
			populate(article);
		}
	}


	private void selectArticle(Article article) {
		if (article != null && article.getEntry() != null) {
            CListBoxEntry entry = inbox.getEntry(article.link);
            inbox.setSelected(entry);
		}
	}
}
