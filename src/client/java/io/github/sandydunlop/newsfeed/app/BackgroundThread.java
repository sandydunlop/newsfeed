package io.github.sandydunlop.newsfeed.app;

import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;

public class BackgroundThread extends Thread {
    private final int SLEEP_TIME = 10000; // 10 seconds
    private final Runnable runnable;
    private boolean checkingForUpdates = false;
    private RssFeed rssFeed = null;



    public BackgroundThread() {
        this.runnable = () -> {
            try {
                do {
                    // Placeholder for background task
                    System.out.println("Background thread: tick");
                    checkForUpdates();
                    Thread.sleep(SLEEP_TIME);
                } while (true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    // public BackgroundThread(Runnable runnable) {
    //     this.runnable = runnable;
    // }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void checkForUpdates() {
        if (checkingForUpdates) {
            System.out.println("Already checking for updates, skipping this tick.");
            return;
        }
        checkingForUpdates = true;

        if (rssFeed == null) {
            System.out.println("Initializing...");
            rssFeed = new RssFeed();
            rssFeed.init();
            addToInbox(rssFeed.usedEntries);
        }
        System.out.println("Checking for updates...");
        rssFeed.fetch();
        addToInbox(rssFeed.currentEntries);
        checkingForUpdates = false;
    }


    private void addToInbox(List<SyndEntry> entries) {
        for (SyndEntry entry : entries) {
            Article article = Article.of(entry);
            Inbox.getInstance().addArticle(article);
            System.out.println("Added article to inbox: " + article.title);
        }
        Inbox.getInstance().updateEnded();
    }   
}
