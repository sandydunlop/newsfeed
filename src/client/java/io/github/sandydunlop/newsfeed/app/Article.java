package io.github.sandydunlop.newsfeed.app;

import com.rometools.rome.feed.synd.SyndEntry;

import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;


public class Article {
    public String title;
    public String description;
    public String link;
    public SyndEntry syndEntry;


    public static Article of(SyndEntry entry) {
        String title = "";
        String description = "";
        if (entry.getTitle() != null) {
            title = entry.getTitle();
        }
        if (entry.getDescription() != null) {
            description = entry.getDescription().getValue();
        }
        Article article = new Article(title, description, entry.getLink(), entry);
        return article;
    }


    public static Article empty() {
        return new Article();
    }


    private Article() {
        this.title = "";
        this.description = "";
        this.link = "";
    }


    private Article(String title, String description, String link, SyndEntry syndEntry) {
        this.syndEntry = syndEntry;
        this.title = title;
        this.description = description;
        this.link = link;
    }


    public SyndEntry getEntry() {
        return syndEntry;
    }
}
