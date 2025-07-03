package io.github.sandydunlop.newsfeed.app;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import io.github.sandydunlop.cupra.common.logging.Logger;
import io.github.sandydunlop.cupra.common.logging.LogManager;
import io.github.sandydunlop.cupra.platform.PlatformServices;


public class RssFeed {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
	List<SyndEntry> currentEntries = new ArrayList<>();
	List<SyndEntry> usedEntries = new ArrayList<>();
	public URL feedSource = null;
	String feedTitle;


    public RssFeed() {
		// Nothing to see here
	}


	public URL getFeedSource() {
		return feedSource;
	}


	public synchronized void init(){
		try{
			if (NewsfeedConfig.feedUrl != null && !NewsfeedConfig.feedUrl.isEmpty() && currentEntries.isEmpty()) {
				feedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(feedSource));
				feedTitle = feed.getTitle();
				List<SyndEntry> entries = feed.getEntries();
				for (SyndEntry entry : entries) {
					usedEntries.add(entry);
				}
				LOGGER.info("{} feed loaded", feedTitle);
			}
		}catch(IOException e){
			LOGGER.error("Invalid feed at {}", feedSource);
		}catch(FeedException e){
			LOGGER.error("FeedException: {}", e.getMessage());
		}
	}


	public synchronized void fetch()
	{
		if (NewsfeedConfig.feedUrl!=null && !NewsfeedConfig.feedUrl.isEmpty()){
			for (SyndEntry entry : currentEntries) {
				usedEntries.add(entry);
			}
			currentEntries.clear();
			URL tryFeedSource = null;
			try {
				tryFeedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
				if (tryFeedSource == null) {
					LOGGER.error("Feed URL is invalid.");
					feedSource = null;
					return;
				}
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(tryFeedSource));
				feedTitle = feed.getTitle();
				List<SyndEntry> entries = feed.getEntries();
				for (SyndEntry entry : entries) {
					if (!alreadyUsed(entry)) {
						LOGGER.info("{}: New article: {}", feed.getTitle(), entry.getTitle());
						currentEntries.add(entry);
						PlatformServices.getInstance().showNotification(entry.getTitle());
					}
				}
				feedSource = tryFeedSource;
			}catch(IOException e){
				LOGGER.error("Problem loading feed: {}", tryFeedSource == null ? "(null)" : tryFeedSource.toString());
				LOGGER.error("{}", e.getMessage());
			}catch(FeedException e){
				LOGGER.error("FeedException: {}", e.getMessage());
			}catch(Exception e){
				LOGGER.error("Error processing feed: {}", e.getMessage());
				LOGGER.error("{}", Arrays.asList(e.getStackTrace()));
			}
		}
	}
	

	public SyndEntry getEntry(int n) {
		if (usedEntries.size() > n) {
			return usedEntries.get(n);
		}
		return null;
	}


	private boolean alreadyUsed(SyndEntry entry) {
		for(SyndEntry e : usedEntries){
			if(e.getLink().equals(entry.getLink())) {
				return true;
			}
		}
		return false;
	}
}
