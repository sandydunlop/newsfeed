package io.github.sandydunlop.newsfeed.app;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

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


	public void init(){
		try{
			if (NewsfeedConfig.feedUrl != null && !NewsfeedConfig.feedUrl.isEmpty() && currentEntries.isEmpty()) {
				int suppressedCount = 0;
				feedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(feedSource));
				feedTitle = feed.getTitle();
				List<SyndEntry> entries = feed.getEntries();
				for (SyndEntry entry : entries) {
					usedEntries.add(entry);
					suppressedCount++;
				}
				LOGGER.info("{} feed loaded. Suppressing {} old articles.", feedTitle, suppressedCount);
			}
		}catch(IOException e){
			LOGGER.error("Invalid feed at {}", feedSource);
		}catch(FeedException e){
			LOGGER.error("FeedException: {}", e.getMessage());
		}
	}


	public void fetch()
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
				String msg = String.format("Invalid feed at %s", tryFeedSource.toString(), null);
				LOGGER.error(msg);
				PlatformServices.getInstance().showNotification(msg);
			}catch(FeedException e){
				LOGGER.error("FeedException1: {}", e.getMessage());
			} 
		}
	}
	

	public SyndEntry getEntry(int n) {
		if (usedEntries.size() > n) {
			return usedEntries.get(n);
		}
		return null;
	}


	private boolean alreadyGot(SyndEntry entry) {
		for(SyndEntry e : currentEntries){
			if(e.getLink().equals(entry.getLink())) {
				return true;
			}
		}
		return alreadyUsed(entry);
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
