package io.github.sandydunlop.newsfeed.app;

import java.util.Date;
import java.util.List;

import io.github.sandydunlop.cupra.common.CupraScreen;
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
import net.minecraft.util.math.ColorHelper;


public class MainScreen extends CupraScreen implements RssUpdateListener {
	private int articleIndex = -1;
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
	long lastUpdateTime = 0;

    
	public MainScreen() {
		super();

		final int SMALL_VERTICAL_GAP = 5;
        this.setPadding(5);
        this.setAlignHorizontal(Align.Horizontal.SPREAD);

		setTitle("Newsfeed");

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
			article =  (Article)selectionChanged.getValue();
			System.out.println("Selected index: " + inbox.getSelectedIndex());
			populate(article);
			return;
        });
        inbox.setExpandable(true);

		new CSpacer(body, SMALL_VERTICAL_GAP);

		descriptionWidget = new CFormattedLabel(body);
        descriptionWidget.setExpandable(true);

		int halfway = ColorHelper.lerp(0.5f, 
				CWidget.getPalette().REGULAR_BACKGROUND, 
				CWidget.getPalette().INPUT_BACKGROUND);
		descriptionWidget.setBackgroundColor(halfway);

		prevButton = new CButton(footer, "Prev", click -> {
			inbox.setSelectedIndex(inbox.getSelectedIndex() + 1);
		});

		nextButton = new CButton(footer, "Next", click -> {
			inbox.setSelectedIndex(inbox.getSelectedIndex() - 1);
		});

		openButton = new CButton(footer, "Open Link", click -> {
			CListBoxEntry selected = inbox.getSelected();
            article = (Article)selected.getValue();
            PlatformServices.getInstance().openLinkInBrowser(article.link);
		});

		optionsButton = new CButton(footer, "Options", click -> {
            PlatformServices.getInstance().getApp().openScreen(this, Newsfeed.getConfigScreen());
		});

		closeButton = new CButton(footer, "Close", click -> {
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
			articleIndex = inbox.getIndexOf(selected);
		} else {
			articleIndex = -1;
		}
		populate(article);
	}


	private void populate(Article article){
		if (article == null) {
			article = Article.empty();
		}
        //titleWidget.setText(rssFeed.feedTitle);
        titleWidget.setText("Newsfeed");
        
		CLabel heading = new CLabel(null, article.title).bold();
		descriptionWidget.clear();
        descriptionWidget.add(heading);
        descriptionWidget.add(CFormattedLabel.newLine());
        descriptionWidget.add(CFormattedLabel.newLine());
        descriptionWidget.add(new CLabel(null, article.description));
        descriptionWidget.layout();

        prevButton.setEnabled(inbox.getSelectedIndex() < inbox.count() - 1);
        nextButton.setEnabled(inbox.getSelectedIndex() > 0);
    }


	@Override
	public void feedUpdated(RssUpdateEvent event) {
		updateInbox();
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
			if (inbox.count() > 0 && articleIndex == -1) {
				articleIndex = 0;
				inbox.setSelected(latest);
			}
			//PlatformServices.getInstance().render();
		}
		lastUpdateTime = new Date().getTime();
	}


	private void selectArticle(Article article) {
		if (article != null && article.getEntry() != null) {
            CListBoxEntry entry = inbox.getEntry(article.getKey());
            inbox.setSelected(entry);
		}
	}
}
