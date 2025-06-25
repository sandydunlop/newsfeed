package io.github.sandydunlop.newsfeed.app;

import java.util.Date;
import java.util.List;

import io.github.sandydunlop.cupra.common.CupraScreen;
import io.github.sandydunlop.cupra.common.palette.ColorPalette;
import io.github.sandydunlop.cupra.common.util.Align;
import io.github.sandydunlop.cupra.common.widgets.CButton;
import io.github.sandydunlop.cupra.common.widgets.CContainer;
import io.github.sandydunlop.cupra.common.widgets.CFormattedLabel;
import io.github.sandydunlop.cupra.common.widgets.CLabel;
import io.github.sandydunlop.cupra.common.widgets.CListBox;
import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;
import io.github.sandydunlop.cupra.common.widgets.CSpacer;
import io.github.sandydunlop.cupra.common.widgets.CWidget;
import io.github.sandydunlop.cupra.platform.PlatformServices;


public class MainScreen extends CupraScreen implements RssUpdateListener {
	private Article article;
	private CLabel titleWidget;
	private CListBox inbox;
	private CFormattedLabel articleText;
	private CButton prevButton;
	private CButton nextButton;
	private long lastUpdateTime = 0;
	private boolean articleIsSelected = false;

    
	public MainScreen() {
		super();

		final int SMALL_VERTICAL_GAP = 5;
        this.setPadding(5);
        this.setAlignHorizontal(Align.Horizontal.SPREAD);

		setTitle("Newsfeed");

        CContainer header = new CContainer(this, true);
        header.setPadding(4);

        CContainer body = new CContainer(this);
        body.setPadding(0);
        body.setExpandable(true);

        CContainer footer = new CContainer(this, true);
        footer.setAlignHorizontal(Align.Horizontal.SPREAD);  //TODO: Make this work
        footer.setPadding(10);

        titleWidget = new CLabel(header, "")
                .fontSize(22)
                .bold();
        titleWidget.setHeight(28);

        inbox = new CListBox(body, selectionChanged -> {
			article =  (Article)selectionChanged.getValue();
			populate();
			return;
        });
        inbox.setExpandable(true);

		new CSpacer(body, SMALL_VERTICAL_GAP);

		articleText = new CFormattedLabel(body);
        articleText.setExpandable(true);

		int halfway = ColorPalette.argbLerp(0.5f,
				CWidget.getPalette().REGULAR_BACKGROUND, 
				CWidget.getPalette().INPUT_BACKGROUND);
		articleText.setBackgroundColor(halfway);

		prevButton = new CButton(footer, "Prev", click -> {
			inbox.setSelectedIndex(inbox.getSelectedIndex() + 1);
		});

		nextButton = new CButton(footer, "Next", click -> {
			inbox.setSelectedIndex(inbox.getSelectedIndex() - 1);
		});

		new CButton(footer, "Open Link", click -> {
			CListBoxEntry selected = inbox.getSelected();
            article = (Article)selected.getValue();
            PlatformServices.getInstance().openLinkInBrowser(article.link);
		});

		new CButton(footer, "Options", click -> {
            PlatformServices.getInstance().getApp().openScreen(this, Newsfeed.getConfigScreen());
		});

		new CButton(footer, "Close", click -> {
			this.close();
		});

		Inbox.getInstance().addRssUpdateListener(this);
    }


	@Override
	public void onShow() {
		lastUpdateTime = new Date().getTime();
		article = Article.empty();
		CListBoxEntry selected = inbox.getSelected();
		if (selected != null) {
			article = (Article) selected.getValue();
		}
		populate();
	}


	@Override
	public void feedUpdated(RssUpdateEvent event) {
		updateInbox();
	}


	private void populate() {
		if (article == null) {
			article = Article.empty();
		}
        //titleWidget.setText(rssFeed.feedTitle);
        titleWidget.setText("Newsfeed");
        
		CLabel heading = new CLabel(null, article.title).bold();
		articleText.clear();
        articleText.add(heading);
        articleText.add(CFormattedLabel.newLine());
        articleText.add(CFormattedLabel.newLine());
        articleText.add(new CLabel(null, article.description));
        articleText.layout();

        prevButton.setEnabled(inbox.getSelectedIndex() < inbox.count() - 1);
        nextButton.setEnabled(inbox.getSelectedIndex() > 0);
		PlatformServices.getInstance().render();
    }


	private synchronized void updateInbox() {
		List<Article> recentArticles = Inbox.getInstance().getArticlesSince(lastUpdateTime);
		if (!recentArticles.isEmpty()) {
			for (int i = recentArticles.size() - 1; i >= 0; i--) {
				Article newArticle = recentArticles.get(i);
				inbox.insertAt(0, new CListBoxEntry(newArticle.title, newArticle.getKey(), newArticle));
			}
			CListBoxEntry latest = inbox.getEntry(0);
			inbox.scrollTo(latest);
			if (inbox.count() > 0 && !articleIsSelected) {
				inbox.setSelected(latest);
				article = (Article) latest.getValue();
				articleIsSelected = true;
			}
			populate();
		}
		lastUpdateTime = new Date().getTime();
	}
}
