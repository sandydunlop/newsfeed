package io.github.sandydunlop.newsfeed.app;

import java.util.ArrayList;
import java.util.List;

public class Inbox {
    private static Inbox instance = null;
    private List<Article> articles = new ArrayList<>();
	private List<RssUpdateListener> listeners = new ArrayList<>();
    private int articleCount = 0;

    public static Inbox getInstance() {
        if (instance == null) {
            instance = new Inbox();
        }
        return instance;
    }

    private Inbox() {
        // Private constructor to prevent instantiation
    }

    public void addArticle(Article article) {
        // Logic to add an article to the inbox
        articles.add(article); //TODO use getKey to avoid duplicates
        //System.out.println("Article added: " + article.title);
    }


    public void updateEnded() {
        if (articles.size() > articleCount) {
            articleCount = articles.size();
            RssUpdateEvent event = new RssUpdateEvent(this);
            fireRssUpdateEvent(event);
        }
    }


    public List<Article> getArticlesSince(long timestamp) {
        List<Article> recentArticles = new ArrayList<>();
        for (Article article : articles) {
            if (article.revceivedDate > timestamp) {
                recentArticles.add(article);
            }
        }
        return recentArticles;
    }



	public void removeRssUpdateListener(RssUpdateListener listener) {
		listeners.remove(listener);
	}


	public void addRssUpdateListener(RssUpdateListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}


	private void fireRssUpdateEvent(RssUpdateEvent event) {
		for (RssUpdateListener listener : listeners) {
			listener.feedUpdated(event);
		}
	}
}
