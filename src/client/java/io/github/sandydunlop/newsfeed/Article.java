package io.github.sandydunlop.newsfeed;

import com.rometools.rome.feed.synd.SyndEntry;


public class Article {
    public String title;
    public String description;
    public String link;

    public static Article of(SyndEntry entry) {
        String title = "";
        String description = "";
        if (entry.getTitle() != null) {
            title = entry.getTitle();
        }
        if (entry.getDescription() != null) {
            description = entry.getDescription().getValue();
        }
        Article article = new Article(title, description, entry.getLink());
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

    private Article(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }
}
