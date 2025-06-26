package io.github.sandydunlop.newsfeed.app;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rometools.rome.feed.synd.SyndEntry;


public class BackgroundThread extends Thread {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
    private final int SLEEP_TIME = 10000; // 10 seconds
    private final Runnable runnable;
    private boolean checkingForUpdates = false;
    private boolean foundUpdates = false;
    private boolean keepRunning = true;
    private RssFeed rssFeed = null;
    private String lastFeedUrl = "";


    public BackgroundThread() {
        super("Newsfeed-Updt");
        this.runnable = () -> {
            do {
                try {
                    checkForUpdates();
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ignore) {
                    // Ignore
                }
            } while (keepRunning);
        };
    }


    public void restart(){
        if (this.isAlive()) {
            this.interrupt();
        }
    }


    public void terminate(){
        if (this.isAlive()) {
            this.interrupt();
            this.keepRunning = false;
        }
    }


    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean urlHasChanged(){
        return rssFeed != null && 
                rssFeed.getFeedSource() != null && 
                !rssFeed.getFeedSource().toString().equals(lastFeedUrl);
    }
    

    private void checkForUpdates() {
        LOGGER.info("Checking for updates");
        if (checkingForUpdates) {
            return;
        }
        checkingForUpdates = true;

        if (rssFeed == null || urlHasChanged()) {
            rssFeed = new RssFeed();
            rssFeed.init();
            addToInbox(rssFeed.usedEntries);
            lastFeedUrl = rssFeed.getFeedSource().toString();
        }
        rssFeed.fetch();
        addToInbox(rssFeed.currentEntries);
        if (foundUpdates) {
            Inbox.getInstance().updateEnded();
            foundUpdates = false;
        }
        checkingForUpdates = false;
    }


    private void addToInbox(List<SyndEntry> entries) {
        for (SyndEntry entry : entries) {
            Article article = Article.of(entry);
            Inbox.getInstance().addArticle(article);
            foundUpdates = true;
        }
    }   
}
