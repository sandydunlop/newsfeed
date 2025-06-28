package io.github.sandydunlop.newsfeed.app;

import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;

import io.github.sandydunlop.cupra.common.logging.Logger;
import io.github.sandydunlop.cupra.common.logging.LogManager;


public class BackgroundThread extends Thread {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
    private static final int SLEEP_TIME = 60000; // One minute
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
                    LOGGER.debug("Interrupted");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            } while (keepRunning);
            LOGGER.debug("Terminating");
        };
    }


    public void restart(){
        if (this.isAlive()) {
            checkingForUpdates = false;
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
    

    private void checkForUpdates() {
        if (checkingForUpdates) {
            return;
        }
        checkingForUpdates = true;
        if (rssFeed == null || !NewsfeedConfig.feedUrl.equals(lastFeedUrl)) {
            LOGGER.debug("Initializing {}", NewsfeedConfig.feedUrl);
            rssFeed = new RssFeed();
            rssFeed.init();
            addToInbox(rssFeed.usedEntries);
            lastFeedUrl = NewsfeedConfig.feedUrl;
        } else {
            LOGGER.debug("Checking for updates");
            rssFeed.fetch();
            addToInbox(rssFeed.currentEntries);
        }
        if (foundUpdates) {
            LOGGER.debug("New articles found");
            Inbox.getInstance().updateEnded();
            foundUpdates = false;
        }
        LOGGER.debug("Complete");
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
